package com.example.demo.service;

import com.example.demo.dto.CommentDto;
import com.example.demo.dto.PostDto;
import com.example.demo.entity.*;
import com.example.demo.mapper.PostMapper;
import com.example.demo.repo.*;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

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
        this.postMapper=postMapper;
    }

    // ================= CREATE POST =================

    @Override
    public void createPost(String username,
                           String content,
                           String hashtags,
                           String scheduledAt) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = new Post();
        post.setContent(content);
        post.setUser(user);

        post.setPinned(false);
        post.setPinnedAt(null);

        // Always set created time as now
        post.setCreatedAt(LocalDateTime.now());

        // If scheduled time exists → save it in scheduledAt column
        if (scheduledAt != null && !scheduledAt.isBlank()) {

            LocalDateTime scheduledDateTime =
                    LocalDateTime.parse(scheduledAt);

            post.setScheduledAt(scheduledDateTime);

        } else {
            post.setScheduledAt(null);
        }

        postRepository.save(post);

        // Save hashtags
        saveHashtags(post, hashtags);
    }

    // ================= UPDATE POST =================

    @Override
    public void updatePost(Long postId, String content, String hashtags, String username) {

        Post post = postRepository.findById(postId)
                .orElseThrow();

        if (!post.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You cannot edit this post");
        }

        post.setContent(content);
        postRepository.save(post);

        // remove old hashtags
        postHashtagRepository.deleteByPost(post);

        // save new hashtags
        saveHashtags(post, hashtags);
    }

    // ================= DELETE POST =================
    @Override
    @Transactional
    public void deletePost(Long postId, String username) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // Security check
        if (!post.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You are not authorized to delete this post");
        }

        // Just delete post
        postRepository.delete(post);
    }

    // ================= SHARE POST =================

    @Override
    public void sharePost(Long postId, String username) {

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post originalPost = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Post sharedPost = new Post();
        sharedPost.setContent("🔁 Shared from @" 
                + originalPost.getUser().getUsername()
                + "\n\n" + originalPost.getContent());
        sharedPost.setCreatedAt(LocalDateTime.now());
        sharedPost.setUser(currentUser);

       
        sharedPost.setPinned(false);
        sharedPost.setPinnedAt(null);

        postRepository.save(sharedPost);

        
    }
    // ================= TOGGLE LIKE =================

    @Override
    public void toggleLike(Long postId, String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow();

        Post post = postRepository.findById(postId)
                .orElseThrow();

        Optional<Like> existingLike =
                likeRepository.findByUserAndPost(user, post);

        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
        } else {

            Like like = new Like();
            like.setUser(user);
            like.setPost(post);
            likeRepository.save(like);

            if (!post.getUser().getUsername().equals(username)) {
                notificationService.createNotification(
                        post.getUser().getUsername(),
                        username,
                        "LIKE",
                        post.getId()
                );
            }
        }
    }

    // ================= GET POST BY ID =================


    @Override
    public PostDto getPostById(Long postId, String currentUsername) {

        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow();

        Post post = postRepository.findById(postId)
                .orElseThrow();

        long likeCount = likeRepository.countByPost(post);

        boolean liked = likeRepository
                .findByUserAndPost(currentUser, post)
                .isPresent();

        List<CommentDto> comments =
                commentRepository
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
                                            .equals(currentUsername)
                            );
                            return cd;
                        })
                        .toList();

        return postMapper.toDto(post, currentUser, likeCount, liked, comments);
    }


    // ================= SEARCH BY HASHTAG =================
    @Override
    public List<PostDto> searchPostsByHashtag(String tag, String username) {

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Post> posts =
                postRepository
                .findByPostHashtags_Hashtag_NameAndCreatedAtLessThanEqualOrderByCreatedAtDesc(
                        tag,
                        LocalDateTime.now()
                );

        return posts.stream()
                .map(post -> {

                    long likeCount = likeRepository.countByPost(post);

                    boolean liked =
                            likeRepository.findByUserAndPost(currentUser, post)
                                    .isPresent();

                    List<CommentDto> comments =
                            commentRepository
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
                                    .toList();

                    return postMapper.toDto(post, currentUser, likeCount, liked, comments);
                })
                .toList();
    }

    // ================= USER POSTS =================

    @Override
    public List<PostDto> getUserPosts(String profileUsername, String currentUsername) {

        User profileUser = userRepository.findByUsername(profileUsername)
                .orElseThrow();

        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow();

        return postRepository
                .findByUserAndCreatedAtLessThanEqualOrderByPinnedDescCreatedAtDesc(
                        profileUser,
                        LocalDateTime.now()
                )
                .stream()
                .map(post -> {

                    long likeCount = likeRepository.countByPost(post);

                    boolean liked = likeRepository
                            .findByUserAndPost(currentUser, post)
                            .isPresent();

                    List<CommentDto> comments =
                            commentRepository
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
                                                        .equals(currentUsername)
                                        );
                                        return cd;
                                    })
                                    .toList();

                    return postMapper.toDto(post, currentUser, likeCount, liked, comments);
                })
                .toList();
    }

    // ================= ALL POSTS =================

    @Override
    public List<PostDto> getAllPosts() {

        return postRepository
                .findAllByCreatedAtLessThanEqualOrderByCreatedAtDesc(
                        LocalDateTime.now()
                )
                .stream()
                .map(post -> {

                    User user = post.getUser();

                    long likeCount = likeRepository.countByPost(post);

                    boolean liked = false; // no current user context here

                    List<CommentDto> comments =
                            commentRepository
                                    .findByPostOrderByCreatedAtAsc(post)
                                    .stream()
                                    .map(comment -> {
                                        CommentDto cd = new CommentDto();
                                        cd.setId(comment.getId());
                                        cd.setUsername(comment.getUser().getUsername());
                                        cd.setContent(comment.getContent());
                                        cd.setCreatedAt(comment.getCreatedAt());
                                        cd.setOwnedByCurrentUser(false);
                                        return cd;
                                    })
                                    .toList();

                    return postMapper.toDto(post, user, likeCount, liked, comments);
                })
                .toList();
    }

    // ================= FEED =================

    @Override
    public List<PostDto> getFeedPosts(String username) {

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow();

        List<User> followedUsers = followRepository
                .findByFollower(currentUser)
                .stream()
                .map(Follow::getFollowing)
                .toList();

        List<User> feedUsers = new ArrayList<>(followedUsers);
        feedUsers.add(currentUser);

        return postRepository
                .findFeedPosts(feedUsers, LocalDateTime.now())
                .stream()
                .map(post -> {

                    long likeCount = likeRepository.countByPost(post);

                    boolean liked =
                            likeRepository.findByUserAndPost(currentUser, post)
                                    .isPresent();

                    List<CommentDto> comments =
                            commentRepository
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
                                                        .equals(username)
                                        );
                                        return cd;
                                    })
                                    .toList();

                    return postMapper.toDto(post, currentUser, likeCount, liked, comments);
                })
                .toList();
    }

    
    // ================= SAVE HASHTAGS =================

    private void saveHashtags(Post post, String hashtags) {

        if (hashtags == null || hashtags.isBlank()) return;

        String[] tags = hashtags.split(",");

        for (String rawTag : tags) {

            String tag = rawTag.trim().replace("#", "").toLowerCase();

            if (tag.isEmpty()) continue;

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
    }

    @Override
    public void pinPost(Long postId, String username) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // Only owner can pin
        if (!post.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You cannot pin this post");
        }

        post.setPinned(true);
        post.setPinnedAt(LocalDateTime.now());

        postRepository.save(post);
    }
	

    @Override
    public void unpinPost(Long postId, String username) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // Only owner can unpin
        if (!post.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You cannot unpin this post");
        }

        post.setPinned(false);
        post.setPinnedAt(null);

        postRepository.save(post);
    }


    @Override
    public List<String> getTrendingHashtags() {

        List<Object[]> results = postHashtagRepository.findTrendingHashtags();

        return results.stream()
                .limit(3)   // ✅ Top 3 only
                .map(row -> (String) row[0])  // hashtag name
                .toList();
    }
   
    @Override
    public List<PostDto> searchPostsByContent(String keyword, String username) {

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Post> posts =
                postRepository
                .findAllByCreatedAtLessThanEqualOrderByCreatedAtDesc(
                        LocalDateTime.now()
                );

        return posts.stream()
                .filter(post ->
                        post.getContent() != null &&
                        post.getContent().toLowerCase()
                                .contains(keyword.toLowerCase())
                )
                .map(post -> {

                    long likeCount = likeRepository.countByPost(post);

                    boolean liked =
                            likeRepository.findByUserAndPost(currentUser, post)
                                    .isPresent();

                    List<CommentDto> comments =
                            commentRepository
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
                                    .toList();

                    return postMapper.toDto(post, currentUser, likeCount, liked, comments);
                })
                .toList();
    }
}
 

