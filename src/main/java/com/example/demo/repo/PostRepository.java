package com.example.demo.repo;

import com.example.demo.entity.Post;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    // ===============================
    // USER PROFILE POSTS (ONLY PUBLISHED)
    // ===============================
    List<Post> findByUserAndCreatedAtLessThanEqualOrderByPinnedDescCreatedAtDesc(User user, LocalDateTime now);


    // ===============================
    // FEED POSTS
    // ===============================
    @Query("""
    	    SELECT p FROM Post p
    	    WHERE p.user IN :users
    	    AND (p.scheduledAt IS NULL OR p.scheduledAt <= :now)
    	    ORDER BY p.createdAt DESC
    	""")
    	List<Post> findFeedPosts(
    	        @Param("users") List<User> users,
    	        @Param("now") LocalDateTime now
    	);



    
    // ===============================
    // ALL POSTS
    // ===============================
    List<Post> findAllByCreatedAtLessThanEqualOrderByCreatedAtDesc(LocalDateTime now);


    // ===============================
    // HASHTAG SEARCH
    // ===============================
    List<Post> findByPostHashtags_Hashtag_NameAndCreatedAtLessThanEqualOrderByCreatedAtDesc(
            String name,
            LocalDateTime now
    );


    // ===============================
    // USERNAME SEARCH
    // ===============================
    List<Post> findByUser_UsernameContainingIgnoreCaseAndCreatedAtLessThanEqualOrderByCreatedAtDesc(
            String username,
            LocalDateTime now
    );


    // ===============================
    // GET POSTS BY USERNAME (PROFILE PAGE)
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
    // FILTER POSTS BY ROLE (INSTRUCTOR/STUDENT)
    // ===============================
    @Query("""
        SELECT p FROM Post p
        WHERE p.user.role = :role
        AND (p.scheduledAt IS NULL OR p.scheduledAt <= :now)
        ORDER BY p.createdAt DESC
    """)
    List<Post> findPostsByAuthorRole(
            @Param("role") Role role,
            @Param("now") LocalDateTime now
    );


    // ===============================
    // PIN COUNT (MAX 3 PINNED POSTS)
    // ===============================
    long countByUserAndPinnedTrue(User user);
    
    Optional<Post> findByUserAndOriginalPost(User user, Post originalPost);
    
 // POST COUNT
    @Query("""
            SELECT COUNT(p) FROM Post p
            WHERE p.user.username = :username
            AND (p.scheduledAt IS NULL OR p.scheduledAt <= :now)
    """)
    long countPostsByUsername(
            @Param("username") String username,
            @Param("now") LocalDateTime now
    );
}