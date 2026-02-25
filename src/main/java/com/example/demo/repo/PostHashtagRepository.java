package com.example.demo.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.*;
import com.example.demo.entity.PostHashtag;

public interface PostHashtagRepository extends JpaRepository<PostHashtag, Long> {

    @Transactional
    void deleteByPost(Post post);
}