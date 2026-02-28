package com.example.demo.service;

import com.example.demo.entity.Post;
import com.example.demo.entity.PostAnalytics;
import com.example.demo.entity.PostView;
import com.example.demo.repo.PostAnalyticsRepository;
import com.example.demo.repo.PostRepository;
import com.example.demo.repo.PostViewRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private PostAnalyticsRepository analyticsRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostViewRepository postViewRepository;

    @InjectMocks
    private AnalyticsService analyticsService;

    private Post post;

    @BeforeEach
    void setup() {

        post = spy(new Post());

        lenient().doReturn(10L).when(post).getId();
    }
    
    // ================= CREATE ANALYTICS =================
    @Test
    void createPostAnalytics_success() {

        when(analyticsRepository.findByPost_Id(10L))
                .thenReturn(Optional.empty());

        analyticsService.createPostAnalytics(post);

        verify(analyticsRepository, times(1))
                .save(any(PostAnalytics.class));
    }

    // NEGATIVE 
    @Test
    void createPostAnalytics_alreadyExists() {

        when(analyticsRepository.findByPost_Id(10L))
                .thenReturn(Optional.of(new PostAnalytics()));

        analyticsService.createPostAnalytics(post);

        verify(analyticsRepository, never())
                .save(any(PostAnalytics.class));
    }

    // ================= INCREMENT LIKE =================
    @Test
    void incrementLikes_callsRepository() {

        analyticsService.incrementLikes(10L);

        verify(analyticsRepository).incrementLikes(10L);
    }

    // ================= COMMENT =================
    @Test
    void incrementComments_callsRepository() {

        analyticsService.incrementComments(10L);

        verify(analyticsRepository).incrementComments(10L);
    }

    // ================= SHARE =================
    @Test
    void incrementShares_callsRepository() {

        analyticsService.incrementShares(10L);

        verify(analyticsRepository).incrementShares(10L);
    }

    // ================= REACH =================
    @Test
    void incrementReach_callsRepository() {

        analyticsService.incrementReach(10L);

        verify(analyticsRepository).incrementReach(10L);
    }

    // ================= RECORD VIEW  =================
    @Test
    void recordView_firstTime_shouldSaveViewAndIncreaseReach() {

        when(postViewRepository.existsByPost_IdAndUserId(10L, 5L))
                .thenReturn(false);

        when(postRepository.findById(10L))
                .thenReturn(Optional.of(post));

        analyticsService.recordView(10L, 5L);

        verify(postViewRepository).save(any(PostView.class));
        verify(analyticsRepository).incrementReach(10L);
    }

    // NEGATIVE (already viewed)
    @Test
    void recordView_alreadyViewed_shouldNotIncreaseReach() {

        when(postViewRepository.existsByPost_IdAndUserId(10L, 5L))
                .thenReturn(true);

        analyticsService.recordView(10L, 5L);

        verify(postViewRepository, never()).save(any());
        verify(analyticsRepository, never()).incrementReach(anyLong());
    }

    // ================= DELETE =================
    @Test
    void deleteAnalytics_shouldCallRepository() {

        analyticsService.deleteAnalytics(10L);

        verify(analyticsRepository).deleteByPostId(10L);
    }

    // ================= UNLIKE =================
    @Test
    void decrementLikes_shouldCallRepository() {

        analyticsService.decrementLikes(10L);

        verify(analyticsRepository).decrementLikes(10L);
    }

    // ================= FETCH =================
    @Test
    void getAnalytics_found() {

        PostAnalytics analytics = new PostAnalytics();

        when(analyticsRepository.findByPost_Id(10L))
                .thenReturn(Optional.of(analytics));

        PostAnalytics result = analyticsService.getAnalyticsByPostId(10L);

        assertNotNull(result);
    }

    // NEGATIVE
    @Test
    void getAnalytics_notFound() {

        when(analyticsRepository.findByPost_Id(10L))
                .thenReturn(Optional.empty());

        PostAnalytics result = analyticsService.getAnalyticsByPostId(10L);

        assertNull(result);
    }
}