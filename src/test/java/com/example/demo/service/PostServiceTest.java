package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.demo.dto.PostRequestDTO;
import com.example.demo.entity.Posts;
import com.example.demo.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostServiceImpl postService;

    @Test
    void testSavePost_Success() {
        // 1. Setup Data
        PostRequestDTO dto = new PostRequestDTO();
        dto.setContent("Testing without logging");
        Long userId = 1L;

        Posts mockSavedPost = new Posts();
        mockSavedPost.setId(99L);
        mockSavedPost.setContent(dto.getContent());

        // 2. Define Mock Behavior
        when(postRepository.save(any(Posts.class))).thenReturn(mockSavedPost);

        // 3. Execute
        Posts result = postService.savePost(dto, userId);

        // 4. Verify results (JUnit)
        assertNotNull(result);
        assertEquals(99L, result.getId());
        assertEquals("Testing without logging", result.getContent());
        
        // Ensure the repository was actually called
        verify(postRepository, times(1)).save(any(Posts.class));
    }
}