package com.example.demo.repo;

import com.example.demo.entity.Post;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // ===============================
    // USER PROFILE POSTS (ONLY PUBLISHED)
    // ===============================
    List<Post> findByUserAndCreatedAtLessThanEqualOrderByPinnedDescCreatedAtDesc(
            User user,
            LocalDateTime now
    );

    // ===============================
    // FEED POSTS (ONLY PUBLISHED)
    // ===============================
    @Query("""
        SELECT DISTINCT p FROM Post p
        LEFT JOIN FETCH p.postHashtags ph
        LEFT JOIN FETCH ph.hashtag
        WHERE p.user IN :users
        AND (p.scheduledAt IS NULL OR p.scheduledAt <= :now)
        ORDER BY p.createdAt DESC
    """)
    List<Post> findFeedPosts(
            @Param("users") List<User> users,
            @Param("now") LocalDateTime now
    );

    // ===============================
    // ALL POSTS (ONLY PUBLISHED)
    // ===============================
    List<Post> findAllByCreatedAtLessThanEqualOrderByCreatedAtDesc(
            LocalDateTime now
    );

    // ===============================
    // HASHTAG SEARCH (CORRECT ONE)
    // ===============================
    List<Post> findByPostHashtags_Hashtag_NameAndCreatedAtLessThanEqualOrderByCreatedAtDesc(
            String name,
            LocalDateTime now
    );

    // ===============================
    // USERNAME SEARCH (ONLY PUBLISHED)
    // ===============================
    List<Post> findByUser_UsernameContainingIgnoreCaseAndCreatedAtLessThanEqualOrderByCreatedAtDesc(
            String username,
            LocalDateTime now
    );

    // ===============================
    // GET POSTS BY USERNAME
    // ===============================
    @Query("""
        SELECT DISTINCT p FROM Post p
        LEFT JOIN FETCH p.postHashtags ph
        LEFT JOIN FETCH ph.hashtag
        WHERE p.user.username = :username
        AND (p.scheduledAt IS NULL OR p.scheduledAt <= :now)
        ORDER BY p.createdAt DESC
    """)
    List<Post> findPostsByUsername(
            @Param("username") String username,
            @Param("now") LocalDateTime now
    );

    // ===============================
    // CONTENT SEARCH
    // ===============================
    List<Post> findByContentContainingIgnoreCase(String keyword);

    // ===============================
    // PIN COUNT (LIMIT 3 LOGIC)
    // ===============================
    long countByUserAndPinnedTrue(User user);
}