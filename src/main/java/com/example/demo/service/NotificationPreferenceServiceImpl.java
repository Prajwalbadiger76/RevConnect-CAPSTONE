package com.example.demo.service;

import com.example.demo.entity.NotificationPreference;
import com.example.demo.entity.User;
import com.example.demo.repo.NotificationPreferenceRepository;
import com.example.demo.repo.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class NotificationPreferenceServiceImpl
        implements NotificationPreferenceService {

    private final NotificationPreferenceRepository repository;
    private final UserRepository userRepository;

    public NotificationPreferenceServiceImpl(
            NotificationPreferenceRepository repository,
            UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Override
    public NotificationPreference getPreferences(String username) {

        User user = userRepository.findByUsername(username).orElseThrow();

        return repository.findByUser(user)
                .orElseGet(() -> {
                    NotificationPreference pref =
                            new NotificationPreference();
                    pref.setUser(user);
                    return repository.save(pref);
                });
    }

    @Override
    public void updatePreferences(String username,
                                  NotificationPreference updated) {

        NotificationPreference pref = getPreferences(username);

        pref.setLikeEnabled(updated.isLikeEnabled());
        pref.setCommentEnabled(updated.isCommentEnabled());
        pref.setFollowEnabled(updated.isFollowEnabled());
        pref.setConnectionEnabled(updated.isConnectionEnabled());

        repository.save(pref);
    }
    
}