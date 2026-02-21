package com.example.demo.repo;

import com.example.demo.entity.Share;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShareRepository extends JpaRepository<Share, Long> {

    long countByPostId(Long postId);
}