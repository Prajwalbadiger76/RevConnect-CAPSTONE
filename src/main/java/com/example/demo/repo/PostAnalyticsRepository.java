package com.example.demo.repo;

import com.example.demo.entity.PostAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PostAnalyticsRepository extends JpaRepository<PostAnalytics, Long> {

    Optional<PostAnalytics> findByPostId(Long postId);
}