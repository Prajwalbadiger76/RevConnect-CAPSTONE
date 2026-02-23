package com.example.demo.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.demo.entity.Post;

@Repository
// CHANGE 'class' TO 'interface'
public interface PostRepository extends JpaRepository<Post, Long> {
    
    List<Post> findByUserIdOrderByCreatedAtDesc(Long userId);
}