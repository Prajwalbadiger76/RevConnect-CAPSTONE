package com.example.demo.service;

import com.example.demo.dto.CommentDto;

import com.example.demo.dto.PostDto;
import com.example.demo.entity.*;
import com.example.demo.exception.InvalidScheduleException;
import com.example.demo.mapper.PostMapper;
import com.example.demo.repo.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.net.URI;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import java.util.regex.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class PostServiceImpl implements PostService {

    private static final Logger logger = LogManager.getLogger(PostServiceImpl.class);

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
        this.postMapper = postMapper;
    }

    // =========================================================
    // CREATE POST
    // =========================================================
    @Override
    public void createPost(String username,
                           String content,
                           String hashtags,
                           String scheduledAt,
                           boolean promotional,
                           String ctaType,
                           String ctaUrl,
                           String productTag) {

        logger.info("Create post request received for user: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("User not found: {}", username);
                    return new RuntimeException("User not found");
                });

        Post post = new Post();
        post.setContent(content);
        post.setUser(user);
        post.setCreatedAt(LocalDateTime.now());
        post.setPinned(false);

        if (promotional) {

            logger.info("Promotional post attempt by user: {}", username);

            if (user.getRole() != Role.BUSINESS &&
                user.getRole() != Role.CREATOR) {

                logger.warn("Unauthorized promotional post attempt by {}", username);
                throw new RuntimeException(
                        "Only Business or Creator can create promotional posts"
                );
            }

            
            if (ctaUrl != null && !ctaUrl.isBlank()) {

                try {

                    URI uri = new URI(ctaUrl);

                    String scheme = uri.getScheme();
                    String host = uri.getHost();

                    // 1️⃣ Scheme validation
                    if (scheme == null ||
                        (!scheme.equalsIgnoreCase("http") &&
                         !scheme.equalsIgnoreCase("https"))) {

                        throw new RuntimeException(
                                "URL must use a protocol"
                        );
                    }

                    // 2️⃣ Host validation
                    if (host == null || host.isBlank()) {
                        throw new RuntimeException("Invalid domain in URL");
                    }

                    // 3️⃣ Domain validation
                    if (!host.matches("^[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
                        throw new RuntimeException("Invalid domain format");
                    }

                } catch (Exception e) {
                    throw new RuntimeException("Invalid URL format");
                }
            }

            // ✅ Set promotional fields
            post.setIsPromotional(true);
            post.setCtaType(ctaType);
            post.setCtaUrl(ctaUrl);
            post.setProductTag(productTag);

        } else {

            post.setIsPromotional(false);
        }
    

        if (scheduledAt != null && !scheduledAt.isBlank()) {

            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

            LocalDateTime scheduledDateTime =
                    LocalDateTime.parse(scheduledAt, formatter);

            if (!scheduledDateTime.isAfter(LocalDateTime.now())) {
                logger.error("Invalid schedule time provided by user: {}", username);
                throw new InvalidScheduleException(
                        "Scheduled time must be in the future"
                );
            }

            post.setScheduledAt(scheduledDateTime);
        }

        Post savedPost = postRepository.save(post);
        analyticsService.createPostAnalytics(savedPost);
        
        // HASHTAGS
        // =========================
        if (hashtags != null && !hashtags.isBlank()) {
            parseHashtags(savedPost, hashtags);
        }

        logger.info("Post created successfully for user: {}", username);
    }
    // =========================================================
    // UPDATE POST
    // =========================================================
    @Override
    public void updatePost(Long postId,
                           String content,
                           String hashtags,
                           String username) {

        logger.info("Update request for post {} by user {}", postId, username);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    logger.error("Post not found for update: {}", postId);
                    return new RuntimeException("Post not found");
                });

        if (!post.getUser().getUsername().equals(username)) {
            logger.warn("Unauthorized update attempt on post {} by {}", postId, username);
            throw new RuntimeException("You cannot edit this post");
        }

        post.setContent(content);
        postRepository.save(post);

        postHashtagRepository.deleteByPost(post);

        if (hashtags != null && !hashtags.isBlank()) {
            parseHashtags(post, hashtags);
        }

        logger.info("Post {} updated successfully", postId);
    }

    // =========================================================
    // DELETE POST
    // =========================================================
    @Override
    public void deletePost(Long postId, String username) {

        logger.info("Delete request for post {} by user {}", postId, username);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    logger.error("Post not found for delete: {}", postId);
                    return new RuntimeException("Post not found");
                });

        if (!post.getUser().getUsername().equals(username)) {
            logger.warn("Unauthorized delete attempt on post {} by {}", postId, username);
            throw new RuntimeException("You cannot delete this post");
        }

        postRepository.delete(post);
        logger.info("Post {} deleted successfully", postId);
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

        // ================= PERSONAL / CREATOR / BUSINESS =================
        if (roleFilter != null && !roleFilter.equalsIgnoreCase("ALL")) {

            Role selectedRole = Role.valueOf(roleFilter.toUpperCase());
            posts = postRepository.findPostsByAuthorRole(selectedRole, now);
        }

        // ================= ALL POSTS (GLOBAL) =================
        else {

            posts = postRepository
                    .findAllByCreatedAtLessThanEqualOrderByCreatedAtDesc(now);
        }

        return posts.stream()
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
                .findPostsByUsername(
                        profileUsername,   // ✅ FIXED HERE
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

        logger.info("Pin request for post {} by {}", postId, username);

        User user = userRepository.findByUsername(username).orElseThrow();

        long pinnedCount = postRepository.countByUserAndPinnedTrue(user);

        if (pinnedCount >= 3) {
            logger.warn("User {} attempted to exceed pin limit", username);
            throw new RuntimeException("Maximum 3 pinned posts allowed");
        }

        Post post = postRepository.findById(postId).orElseThrow();

        if (!post.getUser().getUsername().equals(username)) {
            logger.warn("Unauthorized pin attempt by {}", username);
            throw new RuntimeException("You cannot pin this post");
        }

        post.setPinned(true);
        post.setPinnedAt(LocalDateTime.now());
        postRepository.save(post);

        logger.info("Post {} pinned successfully", postId);
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
                .findByContentContainingIgnoreCase(keyword)  // ✅ FIXED
                .stream()
                .filter(p -> p.getScheduledAt() == null ||
                             p.getScheduledAt().isBefore(LocalDateTime.now()))
                .map(p -> map(p, user))
                .toList();
    }

    // =========================================================
    // TRENDING HASHTAGS
    // =========================================================
    @Override
    public List<String> getTrendingHashtags() {

        return postHashtagRepository.findTrendingHashtags()
                .stream()
                .limit(5)
                .map(row -> (String) row[0])
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
        dto.setPromotional(Boolean.TRUE.equals(post.getIsPromotional()));
        dto.setCtaType(post.getCtaType());
        dto.setCtaUrl(post.getCtaUrl());
        dto.setProductTag(post.getProductTag());
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

        if (hashtags == null || hashtags.isBlank()) return;

        // Split by comma OR space
        String[] rawTags = hashtags.split("[,\\s]+");

        // Use Set to remove duplicates
        Set<String> uniqueTags = Arrays.stream(rawTags)
                .map(tag -> tag.replace("#", "").trim().toLowerCase())
                .filter(tag -> !tag.isEmpty())
                .collect(Collectors.toSet());

        for (String tag : uniqueTags) {

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