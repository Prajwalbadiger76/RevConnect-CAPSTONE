package com.example.demo.mapper;

import com.example.demo.dto.PostDto;
import com.example.demo.entity.Post;

import java.util.stream.Collectors;

public class PostMapper {

    public static PostDto toDto(Post post) {

        PostDto dto = new PostDto();
        dto.setId(post.getId());
        dto.setContent(post.getContent());
        dto.setPinned(post.isPinned());
        dto.setPromotional(post.isPromotional());
        dto.setRepost(post.isRepost());
        dto.setCtaLink(post.getCtaLink());
        dto.setScheduledTime(post.getScheduledTime());

        if (post.getPostHashtags() != null) {
            dto.setHashtags(
                    post.getPostHashtags()
                            .stream()
                            .map(ph -> ph.getHashtag().getName())
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }
}