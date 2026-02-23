package com.example.demo.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.demo.entity.Posts;

@Repository
// CHANGE 'class' TO 'interface'
public interface PostRepository extends JpaRepository<Posts, Long> {
    
    List<Posts> findByUserIdOrderByCreatedAtDesc(Long userId);
}