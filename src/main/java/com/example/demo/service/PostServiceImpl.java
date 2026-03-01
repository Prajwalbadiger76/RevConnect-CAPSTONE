package com.example.demo.service;

import com.example.demo.dto.CommentDto;
import com.example.demo.dto.PostDto;
import com.example.demo.entity.*;
import com.example.demo.mapper.PostMapper;
import com.example.demo.repo.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    private final PostMapper postMapper;

    public PostServiceImpl(PostRepository postRepository,
                           HashtagRepository hashtagRepository,
                           PostHashtagRepository postHashtagRepository,
                           UserRepository userRepository,
                           FollowRepository followRepository,
                           LikeRepository likeRepository,
                           CommentRepository commentRepository,
                           NotificationService notificationService,
                           AnalyticsService analyticsService,
                           PostMapper postMapper) {

        this.postRepository = postRepository;
        this.hashtagRepository = hashtagRepository;
        this.postHashtagRepository = postHashtagRepository;
        this.userRepository = userRepository;
        this.followRepository = followRepository;
        this.likeRepository = likeRepository;
        this.commentRepository = commentRepository;
        this.notificationService = notificationService;
        this.analyticsService = analyticsService;
        this.postMapper=postMapper;
    }

    // =========================================================
    // CREATE POST (Controller Compatible)
    // =========================================================

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
        post.setCreatedAt(LocalDateTime.now());
        post.setPinned(false);
        post.setPinnedAt(null);

        if (scheduledAt != null && !scheduledAt.isBlank()) {
            post.setScheduledAt(LocalDateTime.parse(scheduledAt));
        }

        Post savedPost = postRepository.save(post);

        // analytics row
        analyticsService.createPostAnalytics(savedPost);

        // parse hashtags from FIELD (NOT content)
        if (hashtags != null && !hashtags.isBlank()) {
            parseHashtags(savedPost, hashtags);
        }
    }
    // =========================================================
    // UPDATE POST
    // =========================================================
    @Override
    @Transactional
    public void updatePost(Long postId,
                           String content,
                           String hashtags,
                           String username) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You cannot edit this post");
        }

        post.setContent(content);
        postRepository.save(post);

        // 🔥 DELETE OLD HASHTAGS
        postHashtagRepository.deleteByPost(post);

        // 🔥 PARSE FROM HASHTAG FIELD
        if (hashtags != null && !hashtags.isBlank()) {
            parseHashtags(post, hashtags);
        }
    }

    // =========================================================
    // DELETE POST
    // =========================================================

    @Override
    @Transactional
    public void deletePost(Long postId, String username) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // Security check: only owner can delete
        if (!post.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You cannot delete this post");
        }

        postRepository.delete(post);
    }
    // =========================================================
    // LIKE / UNLIKE
    // =========================================================

    @Override
    public void toggleLike(Long postId, String username) {

        User user = userRepository.findByUsername(username).orElseThrow();
        Post post = postRepository.findById(postId).orElseThrow();

        Optional<Like> existing = likeRepository.findByUserAndPost(user, post);

        if (existing.isPresent()) {
            likeRepository.delete(existing.get());
            analyticsService.decrementLikes(postId);
        } else {

            Like like = new Like();
            like.setUser(user);
            like.setPost(post);
            likeRepository.save(like);

            analyticsService.incrementLikes(postId);

            if (!post.getUser().getUsername().equals(username)) {
                notificationService.createNotification(
                        post.getUser().getUsername(),
                        username,
                        "LIKE",
                        postId
                );
            }
        }
    }

    // =========================================================
    // SHARE
    // =========================================================

    @Override
    public void sharePost(Long postId, String username) {

        User currentUser = userRepository.findByUsername(username).orElseThrow();
        Post post = postRepository.findById(postId).orElseThrow();

        // 🔥 Resolve base/original post
        Post basePost = post.getOriginalPost() != null
                ? post.getOriginalPost()
                : post;

        // 🔥 Check if already shared
        Optional<Post> existingShare =
                postRepository.findByUserAndOriginalPost(currentUser, basePost);

        if (existingShare.isPresent()) {
            return; // silently ignore duplicate share
        }

        // 🔥 Create shared post
        Post shared = new Post();
        shared.setContent(basePost.getContent());
        shared.setOriginalPost(basePost);
        shared.setUser(currentUser);
        shared.setCreatedAt(LocalDateTime.now());
        shared.setOriginalPost(basePost);

        postRepository.save(shared);

        analyticsService.incrementShares(basePost.getId());

        if (!basePost.getUser().getUsername().equals(username)) {
            notificationService.createNotification(
                    basePost.getUser().getUsername(),
                    username,
                    "SHARE",
                    basePost.getId()
            );
        }
    }

    // =========================================================
    // FEED
    // =========================================================

    @Override
    public List<PostDto> getFeedPosts(String username, String roleFilter) {

        User currentUser = userRepository.findByUsername(username).orElseThrow();
        LocalDateTime now = LocalDateTime.now();

        List<Post> posts;

        // ================= FILTER APPLIED =================
        if (roleFilter != null && !roleFilter.equalsIgnoreCase("ALL")) {

            Role selectedRole = Role.valueOf(roleFilter.toUpperCase());

            posts = postRepository.findPostsByAuthorRole(selectedRole, now);

        }
        // ================= DEFAULT NORMAL FEED =================
        else {

            List<User> followedUsers = followRepository.findByFollower(currentUser)
                    .stream()
                    .map(Follow::getFollowing)
                    .toList();

            List<User> feedUsers = new ArrayList<>(followedUsers);
            feedUsers.add(currentUser);

            posts = postRepository.findFeedPosts(feedUsers, now);
        }

        return posts.stream()

                // 🔥 Hide original post if current user already reshared it
                .filter(post -> {
                    if (post.getOriginalPost() == null) {
                        return postRepository
                                .findByUserAndOriginalPost(currentUser, post)
                                .isEmpty();
                    }
                    return true;
                })

                .map(post -> {
                    analyticsService.recordView(post.getId(), currentUser.getId());
                    return map(post, currentUser);
                })
                .toList();
    }
    
    // =========================================================
    // GET POST BY ID
    // =========================================================

    @Override
    public PostDto getPostById(Long postId, String currentUsername) {

        User currentUser = userRepository.findByUsername(currentUsername).orElseThrow();
        Post post = postRepository.findById(postId).orElseThrow();

        return map(post, currentUser);
    }

    // =========================================================
    // USER POSTS
    // =========================================================


    @Override
    public List<PostDto> getPostsByUsername(String profileUsername, String currentUsername) {

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
    // =========================================================
    // ALL POSTS
    // =========================================================

    @Override
    public List<PostDto> getAllPosts() {

        return postRepository
                .findAllByCreatedAtLessThanEqualOrderByCreatedAtDesc(LocalDateTime.now())
                .stream()
                .map(post -> map(post, post.getUser()))
                .toList();
    }

    // =========================================================
    // PIN / UNPIN
    // =========================================================

    @Override
    public void pinPost(Long postId, String username) {

        User user = userRepository.findByUsername(username).orElseThrow();

        long pinnedCount = postRepository
                .countByUserAndPinnedTrue(user);

        if (pinnedCount >= 3) {
            throw new RuntimeException("Maximum 3 pinned posts allowed");
        }

        Post post = postRepository.findById(postId).orElseThrow();

        if (!post.getUser().getUsername().equals(username))
            throw new RuntimeException("You cannot pin this post");

        post.setPinned(true);
        post.setPinnedAt(LocalDateTime.now());

        postRepository.save(post);
    }

    @Override
    public void unpinPost(Long postId, String username) {

        Post post = postRepository.findById(postId).orElseThrow();

        if (!post.getUser().getUsername().equals(username))
            throw new RuntimeException("You cannot unpin this post");

        post.setPinned(false);
        post.setPinnedAt(null);
        postRepository.save(post);
    }

    // =========================================================
    // SEARCH
    // =========================================================
    @Override
    public List<PostDto> searchPostsByHashtag(String tag, String username) {

        if (tag == null || tag.isBlank()) {
            return Collections.emptyList();
        }

        // Remove # if present
        tag = tag.trim().toLowerCase();
        if (tag.startsWith("#")) {
            tag = tag.substring(1);
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return postRepository
                .findByPostHashtags_Hashtag_NameAndCreatedAtLessThanEqualOrderByCreatedAtDesc(
                        tag, LocalDateTime.now())
                .stream()
                .map(post -> map(post, user))
                .toList();
    }

    @Override
    public List<PostDto> searchPostsByContent(String keyword, String username) {

        User user = userRepository.findByUsername(username).orElseThrow();

        return postRepository
                .findAllByCreatedAtLessThanEqualOrderByCreatedAtDesc(LocalDateTime.now())
                .stream()
                .filter(p -> p.getContent() != null &&
                        p.getContent().toLowerCase().contains(keyword.toLowerCase()))
                .map(p -> map(p, user))
                .toList();
    }

    // =========================================================
    // TRENDING HASHTAGS
    // =========================================================

    @Override
    public List<String> getTrendingHashtags() {

        Map<String, Integer> count = new HashMap<>();

        for (Post post : postRepository.findAll()) {
            for (PostHashtag ph : post.getPostHashtags()) {
                String tag = ph.getHashtag().getName();
                count.put(tag, count.getOrDefault(tag, 0) + 1);
            }
        }

        return count.entrySet()
                .stream()
                .sorted((a,b)->b.getValue()-a.getValue())
                .limit(3)
                .map(Map.Entry::getKey)
                .toList();
    }

    // =========================================================
    // DTO MAPPER
    // =========================================================

    private PostDto map(Post post, User currentUser) {

        PostDto dto = new PostDto();

        dto.setId(post.getId());
        dto.setContent(post.getContent());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUsername(post.getUser().getUsername());
        dto.setPinned(post.getPinned());
        dto.setHashtags(
        	    post.getPostHashtags()
        	        .stream()
        	        .map(ph -> ph.getHashtag().getName())
        	        .toList()
        	);

        dto.setLikeCount(likeRepository.countByPost(post));
        dto.setLikedByCurrentUser(likeRepository.findByUserAndPost(currentUser, post).isPresent());

        dto.setComments(commentRepository.findByPostOrderByCreatedAtAsc(post)
                .stream()
                .map(c -> {
                    CommentDto cd = new CommentDto();
                    cd.setId(c.getId());
                    cd.setUsername(c.getUser().getUsername());
                    cd.setContent(c.getContent());
                    cd.setCreatedAt(c.getCreatedAt());
                    cd.setOwnedByCurrentUser(c.getUser().getUsername().equals(currentUser.getUsername()));
                    return cd;
                }).toList());

        PostAnalytics analytics = analyticsService.getAnalyticsByPostId(post.getId());

        if (analytics != null) {
            dto.setTotalComments(analytics.getTotalComments());
            dto.setTotalShares(analytics.getTotalShares());
            dto.setReachCount(analytics.getReachCount());
            dto.setEngagementRate(analytics.getEngagementRate());
        }
        
     // 🔥 SHARE CHECK
        Post basePost = post.getOriginalPost() != null
                ? post.getOriginalPost()
                : post;

        boolean alreadyShared =
                postRepository.findByUserAndOriginalPost(currentUser, basePost)
                              .isPresent();

        dto.setSharedByCurrentUser(alreadyShared);
        
     // SHARE HEADER SUPPORT
        if (post.getOriginalPost() != null) {
            dto.setSharedFromUsername(
                post.getOriginalPost().getUser().getUsername()
            );
        }

        return dto;
    }

    // =========================================================
    // HASHTAG PARSER
    // =========================================================

    private void parseHashtags(Post post, String hashtags) {

        String[] tags = hashtags.split("\\s+");

        for (String rawTag : tags) {

            String tag = rawTag.replace("#", "")
                               .trim()
                               .toLowerCase();

            if (tag.isEmpty()) continue;

            Hashtag hashtag = hashtagRepository
                    .findByName(tag)
                    .orElseGet(() -> {
                        Hashtag newTag = new Hashtag();
                        newTag.setName(tag);
                        return hashtagRepository.save(newTag);
                    });

            PostHashtag postHashtag = new PostHashtag();
            postHashtag.setPost(post);
            postHashtag.setHashtag(hashtag);

            postHashtagRepository.save(postHashtag);
        }
    }
}