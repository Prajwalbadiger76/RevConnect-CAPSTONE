package com.example.demo.repo;

import com.example.demo.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByUser_IdOrderByCreatedAtDesc(Long userId);

    List<Post> findByScheduledTimeIsNullOrScheduledTimeBefore(LocalDateTime time);
}