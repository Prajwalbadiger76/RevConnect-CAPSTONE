package com.example.demo.service;

import com.example.demo.dto.CommentDto;
import com.example.demo.dto.PostDto;
import com.example.demo.entity.*;
import com.example.demo.repo.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class PostServiceImpl implements PostService {

	private final PostRepository postRepository;
	private final HashtagRepository hashtagRepository;
	private final PostHashtagRepository postHashtagRepository;
	private final UserRepository userRepository;
	private final FollowRepository followRepository;
	private final LikeRepository likeRepository;
	private final CommentRepository commentRepository;
	private final NotificationService notificationService;
	private final AnalyticsService analyticsService;

	public PostServiceImpl(PostRepository postRepository, HashtagRepository hashtagRepository,
			PostHashtagRepository postHashtagRepository, UserRepository userRepository,
			FollowRepository followRepository, LikeRepository likeRepository, CommentRepository commentRepository,
			NotificationService notificationService, AnalyticsService analyticsService) {

		this.postRepository = postRepository;
		this.hashtagRepository = hashtagRepository;
		this.postHashtagRepository = postHashtagRepository;
		this.userRepository = userRepository;
		this.followRepository = followRepository;
		this.likeRepository = likeRepository;
		this.commentRepository = commentRepository;
		this.notificationService = notificationService;
		this.analyticsService = analyticsService;
	}

	// ================= CREATE POST =================

	@Override
	public void createPost(String username, String content) {

		User user = userRepository.findByUsername(username).orElseThrow();

		Post post = new Post();
		post.setContent(content);
		post.setUser(user);

		Post savedPost = postRepository.save(post);

		// Create analytics row
		analyticsService.createPostAnalytics(savedPost);
		parseHashtags(savedPost, content);
	}

	// ================= UPDATE POST =================

	@Override
	public void updatePost(Long postId, String username, String content) {

		Post post = postRepository.findById(postId).orElseThrow();

		if (!post.getUser().getUsername().equals(username)) {
			throw new RuntimeException("You cannot edit this post");
		}

		post.setContent(content);
		postRepository.save(post);
	}

	// ================= DELETE POST =================

	@Override
	public void deletePost(Long postId, String username) {

		Post post = postRepository.findById(postId).orElseThrow();

		if (!post.getUser().getUsername().equals(username)) {
			throw new RuntimeException("You cannot delete this post");
		}

		postRepository.delete(post);
	}

	// ================= GET USER POSTS =================

	@Override
	public List<PostDto> getUserPosts(String username) {

		User currentUser = userRepository.findByUsername(username).orElseThrow();

		return postRepository.findByUserOrderByCreatedAtDesc(currentUser).stream().map(post -> map(post, currentUser))
				.collect(Collectors.toList());
	}

	// ================= GET ALL POSTS =================

	@Override
	public List<PostDto> getAllPosts() {

		return postRepository.findAllByOrderByCreatedAtDesc().stream().map(post -> map(post, post.getUser()))
				.collect(Collectors.toList());
	}

	// ================= FEED (RECORD VIEW HERE) =================

	@Override
	public List<PostDto> getFeedPosts(String username) {

		User currentUser = userRepository.findByUsername(username).orElseThrow();

		List<User> followedUsers = followRepository.findByFollower(currentUser).stream().map(Follow::getFollowing)
				.collect(Collectors.toList());

		List<User> feedUsers = new ArrayList<>(followedUsers);
		feedUsers.add(currentUser);

		return postRepository.findByUserInOrderByCreatedAtDesc(feedUsers).stream().map(post -> {

			// ⭐ RECORD UNIQUE VIEW
			analyticsService.recordView(post.getId(), currentUser.getId());

			return map(post, currentUser);
		}).collect(Collectors.toList());
	}

	// ================= LIKE =================

	@Override
	public void toggleLike(Long postId, String username) {

		User user = userRepository.findByUsername(username).orElseThrow();
		Post post = postRepository.findById(postId).orElseThrow();

		Optional<Like> existingLike = likeRepository.findByUserAndPost(user, post);

		if (existingLike.isPresent()) {
			likeRepository.delete(existingLike.get());
		} else {

			Like like = new Like();
			like.setUser(user);
			like.setPost(post);
			likeRepository.save(like);

			// analyticsService.incrementLikes(postId);

			if (!post.getUser().getUsername().equals(username)) {
				notificationService.createNotification(post.getUser().getUsername(), username, "LIKE", postId);
			}
		}
	}

	// ================= SHARE =================

	@Override
	public void sharePost(Long postId, String username) {

		User currentUser = userRepository.findByUsername(username).orElseThrow();
		Post originalPost = postRepository.findById(postId).orElseThrow();

		Post sharedPost = new Post();
		sharedPost.setContent(
				"🔁 Shared from @" + originalPost.getUser().getUsername() + "\n\n" + originalPost.getContent());
		sharedPost.setUser(currentUser);

		postRepository.save(sharedPost);

		analyticsService.incrementShares(postId);

		if (!originalPost.getUser().getUsername().equals(username)) {
			notificationService.createNotification(originalPost.getUser().getUsername(), username, "SHARE", postId);
		}
	}

	// ================= DTO MAPPER =================

	private PostDto map(Post post, User currentUser) {

		PostDto dto = new PostDto();

		dto.setId(post.getId());
		dto.setContent(post.getContent());
		dto.setCreatedAt(post.getCreatedAt());
		dto.setUsername(post.getUser().getUsername());

		dto.setLikeCount(likeRepository.countByPost(post));
		dto.setLikedByCurrentUser(likeRepository.findByUserAndPost(currentUser, post).isPresent());

		List<CommentDto> commentDtos = commentRepository.findByPostOrderByCreatedAtAsc(post).stream().map(comment -> {
			CommentDto cd = new CommentDto();
			cd.setId(comment.getId());
			cd.setUsername(comment.getUser().getUsername());
			cd.setContent(comment.getContent());
			cd.setCreatedAt(comment.getCreatedAt());
			cd.setOwnedByCurrentUser(comment.getUser().getUsername().equals(currentUser.getUsername()));
			return cd;
		}).toList();

		dto.setComments(commentDtos);

		PostAnalytics analytics = analyticsService.getAnalyticsByPostId(post.getId());

		if (analytics != null) {
			dto.setTotalComments(analytics.getTotalComments());
			dto.setTotalShares(analytics.getTotalShares());
			dto.setReachCount(analytics.getReachCount());
			dto.setEngagementRate(analytics.getEngagementRate());
		} else {
			dto.setTotalComments(0);
			dto.setTotalShares(0);
			dto.setReachCount(0);
			dto.setEngagementRate(0.0);
		}

		return dto;
	}

	// ================= HASHTAGS =================

	private void parseHashtags(Post post, String content) {

		Pattern pattern = Pattern.compile("#(\\w+)");
		Matcher matcher = pattern.matcher(content);

		while (matcher.find()) {

			String tag = matcher.group(1).toLowerCase();

			Hashtag hashtag = hashtagRepository.findByName(tag).orElseGet(() -> {
				Hashtag newTag = new Hashtag();
				newTag.setName(tag);
				return hashtagRepository.save(newTag);
			});

			PostHashtag ph = new PostHashtag();
			ph.setPost(post);
			ph.setHashtag(hashtag);

			postHashtagRepository.save(ph);
		}
	}
}