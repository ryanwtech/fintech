package com.fintech.service;

import com.fintech.domain.User;
import com.fintech.dto.UserProfile;
import com.fintech.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ProfileService {

    @Autowired
    private UserRepository userRepository;

    public UserProfile getUserProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return buildUserProfile(user);
    }

    public UserProfile getUserProfileByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return buildUserProfile(user);
    }

    private UserProfile buildUserProfile(User user) {
        UserProfile profile = new UserProfile();
        profile.setId(user.getId());
        profile.setEmail(user.getEmail());
        profile.setUsername(user.getUsername());
        profile.setFirstName(user.getFirstName());
        profile.setLastName(user.getLastName());
        profile.setRole(user.getRole().name());
        profile.setIsActive(user.getIsActive());
        profile.setCreatedAt(user.getCreatedAt());

        // TODO: Add actual counts when repositories are available
        profile.setAccountCount(0L);
        profile.setTransactionCount(0L);
        profile.setCategoryCount(0L);
        profile.setBudgetCount(0L);

        return profile;
    }
}
