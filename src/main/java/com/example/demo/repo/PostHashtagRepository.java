package com.example.demo.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.*;
import com.example.demo.entity.PostHashtag;

public interface PostHashtagRepository extends JpaRepository<PostHashtag, Long> {

    @Transactional
    void deleteByPost(Post post);
    
    @Query("""
    	    SELECT ph.hashtag.name, COUNT(ph)
    	    FROM PostHashtag ph
    	    WHERE ph.post.scheduledAt IS NULL 
    	       OR ph.post.scheduledAt <= CURRENT_TIMESTAMP
    	    GROUP BY ph.hashtag.name
    	    ORDER BY COUNT(ph) DESC
    	""")
    	List<Object[]> findTrendingHashtags();
   
}