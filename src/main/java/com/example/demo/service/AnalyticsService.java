package com.example.demo.service;

import com.example.demo.dto.FollowerGrowthDTO;
import com.example.demo.entity.Post;
import com.example.demo.entity.PostAnalytics;
import com.example.demo.entity.PostView;
import com.example.demo.entity.User;
import com.example.demo.repo.FollowRepository;
import com.example.demo.repo.PostAnalyticsRepository;
import com.example.demo.repo.PostRepository;
import com.example.demo.repo.PostViewRepository;
import com.example.demo.repo.UserRepository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnalyticsService {

    private final PostAnalyticsRepository analyticsRepository;
    private final PostRepository postRepository;
    private final PostViewRepository postViewRepository;
    private final FollowRepository followRepository;
    private final UserRepository userRepository;


    public AnalyticsService(PostAnalyticsRepository analyticsRepository,
                            PostRepository postRepository,
                            PostViewRepository postViewRepository,FollowRepository followRepository,UserRepository userRepository) {
        this.analyticsRepository = analyticsRepository;
        this.postRepository = postRepository;
        this.postViewRepository = postViewRepository;
        this.followRepository = followRepository;
        this.userRepository = userRepository;


    }

    // CREATE ANALYTICS ROW (When post is created)

    @Transactional
    public void createPostAnalytics(Post post) {

        if (analyticsRepository.findByPost_Id(post.getId()).isPresent()) {
            return;
        }

        PostAnalytics analytics = new PostAnalytics();
        analytics.setPost(post);
        analytics.setReachCount(0);
        analytics.setTotalLikes(0);
        analytics.setTotalComments(0);
        analytics.setTotalShares(0);
        analytics.setEngagementRate(0.0);

        analyticsRepository.save(analytics);

        System.out.println(" ANALYTICS ROW CREATED FOR POST -> " + post.getId());
    }

    // LIKE (AFTER_COMMIT SAFE)

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void incrementLikes(Long postId) {

        System.out.println(" incrementLikes DB update");

        analyticsRepository.incrementLikes(postId);
    }

    // COMMENT (AFTER_COMMIT SAFE)

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void incrementComments(Long postId) {

        System.out.println(" incrementComments DB update");
        analyticsRepository.incrementComments(postId);
    }

    // SHARE

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void incrementShares(Long postId) {

        analyticsRepository.incrementShares(postId);

        System.out.println(" incrementShares DB update");
    }

    // REACH (UNIQUE VIEW)

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void incrementReach(Long postId) {

        analyticsRepository.incrementReach(postId);

        System.out.println(" incrementReach DB update");
    }

    // UNIQUE VIEW TRACKING

    @Transactional
    public void recordView(Long postId, Long userId) {

        boolean alreadyViewed =
                postViewRepository.existsByPost_IdAndUserId(postId, userId);

        if (!alreadyViewed) {

            Post post = postRepository.findById(postId)
                    .orElseThrow();

            PostView view = new PostView();
            view.setPost(post);
            view.setUserId(userId);

            postViewRepository.save(view);

            incrementReach(postId);
        }
    }
    
 // ================= DELETE ANALYTICS (WHEN POST DELETED) =================

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteAnalytics(Long postId) {
        analyticsRepository.deleteByPostId(postId);
    }

    // ================= UNLIKE SUPPORT =================

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void decrementLikes(Long postId) {
        analyticsRepository.decrementLikes(postId);
    }

    // FETCH ANALYTICS

    public PostAnalytics getAnalyticsByPostId(Long postId) {
        return analyticsRepository.findByPost_Id(postId).orElse(null);
    }
    
    // followers count analysis
    public List<FollowerGrowthDTO> getFollowerGrowth(String username) {

        // 🔥 convert username → userId here
    	User user = userRepository.findByUsername(username)
    	        .orElseThrow(() -> new RuntimeException("User not found"));

        List<Object[]> rows = followRepository.getFollowerGrowth(user.getId());

        List<FollowerGrowthDTO> result = new ArrayList<>();

        for (Object[] r : rows) {
            result.add(new FollowerGrowthDTO(
                    r[0].toString(),
                    ((Number) r[1]).longValue()
            ));
        }

        return result;
    }
}