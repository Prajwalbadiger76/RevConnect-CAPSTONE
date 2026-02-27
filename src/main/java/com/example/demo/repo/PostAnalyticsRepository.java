package com.example.demo.repo;

import com.example.demo.entity.PostAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostAnalyticsRepository extends JpaRepository<PostAnalytics, Long> {

    Optional<PostAnalytics> findByPost_Id(Long postId);

    //LIKE
    @Modifying
    @Query("""
        update PostAnalytics pa
        set pa.totalLikes = pa.totalLikes + 1,
            pa.engagementRate =
                case
                    when pa.reachCount = 0 then 0
                    else LEAST(
                            ((pa.totalLikes + 1 + pa.totalComments + pa.totalShares) * 100.0)
                            / pa.reachCount,
                            100
                         )
                end,
            pa.updatedAt = CURRENT_TIMESTAMP
        where pa.post.id = :postId
    """)
    void incrementLikes(@Param("postId") Long postId);


    // COMMENT
    @Modifying
    @Query("""
        update PostAnalytics pa
        set pa.totalComments = pa.totalComments + 1,
            pa.engagementRate =
                case
                    when pa.reachCount = 0 then 0
                    else LEAST(
                            ((pa.totalLikes + pa.totalComments + 1 + pa.totalShares) * 100.0)
                            / pa.reachCount,
                            100
                         )
                end,
            pa.updatedAt = CURRENT_TIMESTAMP
        where pa.post.id = :postId
    """)
    void incrementComments(@Param("postId") Long postId);


    //  SHARE
    @Modifying
    @Query("""
        update PostAnalytics pa
        set pa.totalShares = pa.totalShares + 1,
            pa.engagementRate =
                case
                    when pa.reachCount = 0 then 0
                    else LEAST(
                            ((pa.totalLikes + pa.totalComments + pa.totalShares + 1) * 100.0)
                            / pa.reachCount,
                            100
                         )
                end,
            pa.updatedAt = CURRENT_TIMESTAMP
        where pa.post.id = :postId
    """)
    void incrementShares(@Param("postId") Long postId);


    // REACH
    @Modifying
    @Query("""
        update PostAnalytics pa
        set pa.reachCount = pa.reachCount + 1,
            pa.engagementRate =
                case
                    when (pa.reachCount + 1) = 0 then 0
                    else LEAST(
                            ((pa.totalLikes + pa.totalComments + pa.totalShares) * 100.0)
                            / (pa.reachCount + 1),
                            100
                         )
                end,
            pa.updatedAt = CURRENT_TIMESTAMP
        where pa.post.id = :postId
    """)
    void incrementReach(@Param("postId") Long postId);
    
 // DELETE ANALYTICS WHEN POST IS DELETED
    @Modifying
    @Query("delete from PostAnalytics pa where pa.post.id = :postId")
    void deleteByPostId(@Param("postId") Long postId);


    // UNLIKE SUPPORT (DECREASE LIKE COUNT)
    @Modifying
    @Query("""
        update PostAnalytics pa
        set pa.totalLikes =
            case
                when pa.totalLikes <= 0 then 0
                else pa.totalLikes - 1
            end,
            pa.updatedAt = CURRENT_TIMESTAMP
        where pa.post.id = :postId
    """)
    void decrementLikes(@Param("postId") Long postId);

}