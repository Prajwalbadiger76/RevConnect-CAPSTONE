package com.example.demo.mapper;

import com.example.demo.dto.CommentDto;
import com.example.demo.dto.PostDto;
import com.example.demo.entity.Post;
import com.example.demo.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PostMapper {

    public PostDto toDto(Post post,
                         User currentUser,
                         long likeCount,
                         boolean liked,
                         List<CommentDto> comments) {

        PostDto dto = new PostDto();

        dto.setId(post.getId());
        dto.setContent(post.getContent());
        dto.setUsername(post.getUser().getUsername());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setPinned(Boolean.TRUE.equals(post.getPinned()));
        
        

        dto.setLikeCount(likeCount);
        dto.setLikedByCurrentUser(liked);
        dto.setComments(comments);

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