package com.example.demo.repo;

import com.example.demo.entity.PostView;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostViewRepository extends JpaRepository<PostView, Long> {

    boolean existsByPost_IdAndUserId(Long postId, Long userId);
}