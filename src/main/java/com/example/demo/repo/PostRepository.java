package com.example.demo.repo;

import com.example.demo.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByUser_IdOrderByCreatedAtDesc(Long userId);

<<<<<<< Updated upstream
    List<Post> findByScheduledTimeIsNullOrScheduledTimeBefore(LocalDateTime time);
=======
    List<Post> findAllByOrderByCreatedAtDesc();

    List<Post> findByUserInOrderByCreatedAtDesc(List<User> users);
    
    List<Post> findByUserOrderByPinnedDescCreatedAtDesc(User user);

    List<Post> findByPostHashtagsHashtagNameOrderByCreatedAtDesc(String name);
>>>>>>> Stashed changes
}