package com.example.demo.repo;

import com.example.demo.entity.Follow;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
    		    t.follow_date,
    		    SUM(t.new_followers) OVER (ORDER BY t.follow_date) AS total_followers
    		FROM (
    		    SELECT TRUNC(f.CREATED_AT) AS follow_date,
    		           COUNT(*) AS new_followers
    		    FROM FOLLOWS f
    		    WHERE f.FOLLOWING_ID = :userId
    		    GROUP BY TRUNC(f.CREATED_AT)
    		) t
    		ORDER BY t.follow_date
    		""", nativeQuery = true)
    		List<Object[]> getFollowerGrowth(@Param("userId") Long userId);
    		
    		long countByFollowing_Id(Long followingId);
}