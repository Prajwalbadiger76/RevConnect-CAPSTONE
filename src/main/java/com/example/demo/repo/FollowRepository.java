package com.example.demo.repo;

import com.example.demo.entity.Follow;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    Optional<Follow> findByFollowerAndFollowing(User follower, User following);

    List<Follow> findByFollower(User follower);

    List<Follow> findByFollowing(User following);

    void deleteByFollowerAndFollowing(User follower, User following);

    long countByFollowing(User user);  // followers count

    long countByFollower(User user);   // following count
    
    @Query(value = """
    		SELECT 
    		    follow_date,
    		    SUM(new_followers) OVER (ORDER BY follow_date) total_followers
    		FROM (
    		    SELECT TRUNC(created_at) follow_date,
    		           COUNT(*) new_followers
    		    FROM follows
    		    WHERE following_id = :userId
    		    GROUP BY TRUNC(created_at)
    		)
    		""", nativeQuery = true)
    		List<Object[]> getFollowerGrowth(Long userId);
}