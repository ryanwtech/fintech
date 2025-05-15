package com.fintech.web;

import com.fintech.dto.UserProfile;
import com.fintech.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/me")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @GetMapping
    public ResponseEntity<UserProfile> getProfile(Authentication authentication) {
        try {
            String email = authentication.getName();
            // Get user ID from email - in a real app, you'd have a service to get user by email
            // For now, we'll need to modify this to work with the current setup
            UserProfile profile = profileService.getUserProfileByEmail(email);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
