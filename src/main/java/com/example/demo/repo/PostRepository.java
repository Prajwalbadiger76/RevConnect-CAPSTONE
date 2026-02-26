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
    // ALL POSTS (ONLY PUBLISHED)
    // ===============================
    List<Post> findAllByCreatedAtLessThanEqualOrderByCreatedAtDesc(
            LocalDateTime now
    );

    // ===============================
    // HASHTAG SEARCH (ONLY PUBLISHED)
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
    
  

    List<Post> findByContentContainingIgnoreCase(String keyword);
}