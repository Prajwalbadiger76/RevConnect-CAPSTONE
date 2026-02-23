package com.example.demo.repo;

<<<<<<< HEAD
import com.example.demo.entity.PostAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PostAnalyticsRepository extends JpaRepository<PostAnalytics, Long> {

    Optional<PostAnalytics> findByPostId(Long postId);
}
=======
public class PostAnalyticsRepository {

}
>>>>>>> feature/auth-prajwal
