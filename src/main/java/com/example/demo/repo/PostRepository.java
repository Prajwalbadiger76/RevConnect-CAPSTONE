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
	@Query("""
			SELECT p FROM Post p
			WHERE p.user = :user
			AND (
			      p.scheduledAt IS NULL
			      OR p.scheduledAt <= :now
			    )
			ORDER BY p.pinned DESC, p.createdAt DESC
			""")
			List<Post> findProfilePosts(
			        @Param("user") User user,
			        @Param("now") LocalDateTime now
			);

    // ===============================
    // FEED POSTS (ONLY PUBLISHED)
    // ===============================
    @Query("""
    		SELECT p FROM Post p
    		WHERE p.user IN :users
    		AND (
    		      p.scheduledAt IS NULL
    		      OR p.scheduledAt <= :now
    		    )
    		ORDER BY p.pinned DESC, p.createdAt DESC
    		""")
    		List<Post> findFeedPosts(
    		        @Param("users") List<User> users,
    		        @Param("now") LocalDateTime now
    		);

    // ===============================
    // ALL POSTS (ONLY PUBLISHED)
    // ===============================
    @Query("""
    	    SELECT p FROM Post p
    	    WHERE p.scheduledAt IS NULL
    	       OR p.scheduledAt <= :now
    	    ORDER BY p.createdAt DESC
    	""")
    	List<Post> findAllPublishedPosts(
    	        @Param("now") LocalDateTime now
    	);

    // ===============================
    // HASHTAG SEARCH (CORRECT ONE)
    // ===============================
    @Query("""
    	    SELECT p FROM Post p
    	    JOIN p.postHashtags ph
    	    JOIN ph.hashtag h
    	    WHERE h.name = :name
    	    AND (p.scheduledAt IS NULL OR p.scheduledAt <= :now)
    	    ORDER BY p.createdAt DESC
    	""")
    	List<Post> findPublishedByHashtag(
    	        @Param("name") String name,
    	        @Param("now") LocalDateTime now
    	);

    // ===============================
    // USERNAME SEARCH (ONLY PUBLISHED)
    // ===============================
    @Query("""
    	    SELECT p FROM Post p
    	    WHERE LOWER(p.user.username) LIKE LOWER(CONCAT('%', :username, '%'))
    	    AND (p.scheduledAt IS NULL OR p.scheduledAt <= :now)
    	    ORDER BY p.createdAt DESC
    	""")
    	List<Post> findPublishedByUsername(
    	        @Param("username") String username,
    	        @Param("now") LocalDateTime now
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
    @Query("""
    	    SELECT p FROM Post p
    	    WHERE LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%'))
    	    AND (p.scheduledAt IS NULL OR p.scheduledAt <= :now)
    	    ORDER BY p.createdAt DESC
    	""")
    	List<Post> findPublishedByContent(
    	        @Param("keyword") String keyword,
    	        @Param("now") LocalDateTime now
    	);

    // ===============================
    // PIN COUNT (LIMIT 3 LOGIC)
    // ===============================
    long countByUserAndPinnedTrue(User user);
}