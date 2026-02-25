package com.example.demo.service;

import com.example.demo.dto.PostDto;
import com.example.demo.dto.PostRequestDTO;
import com.example.demo.entity.*;
import com.example.demo.mapper.PostMapper;
import com.example.demo.repo.*;
import com.example.demo.exception.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
<<<<<<< Updated upstream
=======
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
>>>>>>> Stashed changes

@Service
public class PostServiceImpl implements PostService {

<<<<<<< Updated upstream
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private HashtagRepository hashtagRepository;

    @Autowired
    private UserRepository userRepository;
=======
    private final PostRepository postRepository;
    private final HashtagRepository hashtagRepository;
    private final PostHashtagRepository postHashtagRepository;
    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final NotificationService notificationService;
    private final PostMapper postMapper;

    public PostServiceImpl(PostRepository postRepository,
                           HashtagRepository hashtagRepository,
                           PostHashtagRepository postHashtagRepository,
                           UserRepository userRepository,
                           FollowRepository followRepository,
                           LikeRepository likeRepository,
                           CommentRepository commentRepository,
                           NotificationService notificationService,
                           PostMapper postMapper) {

        this.postRepository = postRepository;
        this.hashtagRepository = hashtagRepository;
        this.postHashtagRepository = postHashtagRepository;
        this.userRepository = userRepository;
        this.followRepository = followRepository;
        this.likeRepository = likeRepository;
        this.commentRepository = commentRepository;
        this.notificationService = notificationService;
        this.postMapper = postMapper;
    }

    // ================= CREATE POST =================
>>>>>>> Stashed changes

    @Override
    public PostDto createPost(PostRequestDTO dto, Long userId) {

<<<<<<< Updated upstream
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
=======
        User user = userRepository.findByUsername(username).orElseThrow();
>>>>>>> Stashed changes

        Post post = new Post();
        post.setContent(dto.getContent());
        post.setPromotional(dto.isPromotional());
        post.setCtaLink(dto.getCtaLink());
        post.setScheduledTime(dto.getScheduledTime());
        post.setUser(user);

        return PostMapper.toDto(postRepository.save(post));
    }

    @Override
    public PostDto updatePost(Long postId, PostRequestDTO dto, Long userId) {

<<<<<<< Updated upstream
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
=======
        Post post = postRepository.findById(postId).orElseThrow();
>>>>>>> Stashed changes

        if (!post.getUser().getId().equals(userId))
            throw new UnauthorizedActionException("Unauthorized");

<<<<<<< Updated upstream
        post.setContent(dto.getContent());
        post.setScheduledTime(dto.getScheduledTime());

        return PostMapper.toDto(postRepository.save(post));
=======
        post.setContent(content);
        postRepository.save(post);

        // remove old hashtags
        postHashtagRepository.deleteByPost(post);

        // parse new hashtags
        parseHashtags(post, content);
>>>>>>> Stashed changes
    }

    @Override
    public void deletePost(Long postId, Long userId) {

<<<<<<< Updated upstream
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
=======
        Post post = postRepository.findById(postId).orElseThrow();
>>>>>>> Stashed changes

        if (!post.getUser().getId().equals(userId))
            throw new UnauthorizedActionException("Unauthorized");

        postRepository.delete(post);
    }

<<<<<<< Updated upstream
=======
    // ================= SHARE =================

>>>>>>> Stashed changes
    @Override
    public List<PostDto> getFeed() {

<<<<<<< Updated upstream
        List<Post> posts =
                postRepository.findByScheduledTimeIsNullOrScheduledTimeBefore(LocalDateTime.now());

        return posts.stream()
                .map(PostMapper::toDto)
                .toList();
    }

    @Override
    public PostDto pinPost(Long postId, Long userId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        if (!post.getUser().getId().equals(userId))
            throw new UnauthorizedActionException("Unauthorized");

        post.setPinned(true);

        return PostMapper.toDto(postRepository.save(post));
=======
        User currentUser = userRepository.findByUsername(username).orElseThrow();
        Post originalPost = postRepository.findById(postId).orElseThrow();

        Post sharedPost = new Post();
        sharedPost.setContent("🔁 Shared from @" +
                originalPost.getUser().getUsername()
                + "\n\n" + originalPost.getContent());
        sharedPost.setCreatedAt(LocalDateTime.now());
        sharedPost.setUser(currentUser);

        postRepository.save(sharedPost);
    }

    // ================= LIKE =================

    @Override
    public void toggleLike(Long postId, String username) {

        User user = userRepository.findByUsername(username).orElseThrow();
        Post post = postRepository.findById(postId).orElseThrow();

        Optional<Like> existing = likeRepository.findByUserAndPost(user, post);

        if (existing.isPresent()) {
            likeRepository.delete(existing.get());
        } else {
            Like like = new Like();
            like.setUser(user);
            like.setPost(post);
            likeRepository.save(like);
        }
>>>>>>> Stashed changes
    }

    @Override
    public PostDto repostPost(Long postId, Long userId) {

<<<<<<< Updated upstream
        Post original = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Original post not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (original.getUser().getId().equals(userId))
            throw new CustomException("Cannot repost your own post");

        Post repost = new Post();
        repost.setContent(original.getContent());
        repost.setUser(user);
        repost.setRepost(true);
        repost.setOriginalPost(original);
        repost.setPromotional(original.isPromotional());
        repost.setCtaLink(original.getCtaLink());

        return PostMapper.toDto(postRepository.save(repost));
=======
        User currentUser = userRepository.findByUsername(username).orElseThrow();

        return postRepository
                .findByUserOrderByPinnedDescCreatedAtDesc(currentUser)
                .stream()
                .map(post -> buildDto(post, currentUser))
                .collect(Collectors.toList());
    }

    // ================= FEED POSTS =================

    @Override
    public List<PostDto> getFeedPosts(String username) {

        User currentUser = userRepository.findByUsername(username).orElseThrow();

        List<User> followedUsers = followRepository
                .findByFollower(currentUser)
                .stream()
                .map(Follow::getFollowing)
                .collect(Collectors.toList());

        followedUsers.add(currentUser);

        return postRepository
                .findByUserInOrderByCreatedAtDesc(followedUsers)
                .stream()
                .map(post -> buildDto(post, currentUser))
                .collect(Collectors.toList());
    }

    // ================= SEARCH BY HASHTAG =================

    @Override
    public List<PostDto> searchByHashtag(String hashtag) {

        List<Post> posts =
                postRepository.findByPostHashtagsHashtagNameOrderByCreatedAtDesc(
                        hashtag.toLowerCase()
                );

        return posts.stream()
                .map(postMapper::toDto)
                .collect(Collectors.toList());
    }

    // ================= PIN =================

    @Override
    public void pinPost(Long postId, String username) {

        Post post = postRepository.findById(postId).orElseThrow();

        if (!post.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You cannot pin this post");
        }

        post.setPinned(true);
        post.setPinnedAt(LocalDateTime.now());
        postRepository.save(post);
    }

    @Override
    public void unpinPost(Long postId, String username) {

        Post post = postRepository.findById(postId).orElseThrow();

        if (!post.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You cannot unpin this post");
        }

        post.setPinned(false);
        post.setPinnedAt(null);
        postRepository.save(post);
    }

    // ================= PRIVATE HELPER =================

    private PostDto buildDto(Post post, User currentUser) {

        PostDto dto = postMapper.toDto(post);

        dto.setLikeCount(likeRepository.countByPost(post));

        dto.setLikedByCurrentUser(
                likeRepository.findByUserAndPost(currentUser, post).isPresent()
        );

        List<CommentDto> comments = commentRepository
                .findByPostOrderByCreatedAtAsc(post)
                .stream()
                .map(comment -> {
                    CommentDto cd = new CommentDto();
                    cd.setId(comment.getId());
                    cd.setUsername(comment.getUser().getUsername());
                    cd.setContent(comment.getContent());
                    cd.setCreatedAt(comment.getCreatedAt());
                    cd.setOwnedByCurrentUser(
                            comment.getUser().getUsername()
                                    .equals(currentUser.getUsername())
                    );
                    return cd;
                })
                .collect(Collectors.toList());

        dto.setComments(comments);

        return dto;
    }

    // ================= HASHTAG PARSER =================

    private void parseHashtags(Post post, String content) {

        Pattern pattern = Pattern.compile("#(\\w+)");
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {

            String tag = matcher.group(1).toLowerCase();

            Hashtag hashtag = hashtagRepository
                    .findByName(tag)
                    .orElseGet(() -> {
                        Hashtag newTag = new Hashtag();
                        newTag.setName(tag);
                        return hashtagRepository.save(newTag);
                    });

            PostHashtag ph = new PostHashtag();
            ph.setPost(post);
            ph.setHashtag(hashtag);

            postHashtagRepository.save(ph);
        }
>>>>>>> Stashed changes
    }

    @Override
    public List<PostDto> getAllPosts() {

        return postRepository
                .findAllByOrderByCreatedAtDesc()
                .stream()
                .map(postMapper::toDto)
                .toList();
    }
}