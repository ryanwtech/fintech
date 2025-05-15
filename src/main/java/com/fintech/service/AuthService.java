package com.fintech.service;

import com.fintech.config.JwtConfig;
import com.fintech.domain.User;
import com.fintech.dto.*;
import com.fintech.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtConfig jwtConfig;

    @Autowired
    private ProfileService profileService;

    public AuthResponse register(RegisterRequest registerRequest) {
        // Check if email already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Create username from email (part before @)
        String username = registerRequest.getEmail().split("@")[0];
        
        // Ensure username is unique
        String originalUsername = username;
        int counter = 1;
        while (userRepository.existsByUsername(username)) {
            username = originalUsername + counter;
            counter++;
        }

        // Create new user
        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFirstName(registerRequest.getName());
        user.setRole(User.UserRole.USER);
        user.setIsActive(true);

        User savedUser = userRepository.save(user);
        UserProfile profile = profileService.getUserProfile(savedUser.getId());
        
        return new AuthResponse("Registration successful", profile, true);
    }

    public AuthResponse login(LoginRequest loginRequest) {
        Optional<User> userOpt = userRepository.findByEmail(loginRequest.getEmail());
        
        if (userOpt.isPresent() && passwordEncoder.matches(loginRequest.getPassword(), userOpt.get().getPassword())) {
            User user = userOpt.get();
            if (!user.getIsActive()) {
                throw new RuntimeException("Account is deactivated");
            }
            
            UserProfile profile = profileService.getUserProfile(user.getId());
            return new AuthResponse("Login successful", profile, true);
        }
        
        throw new RuntimeException("Invalid credentials");
    }

    public String generateToken(String email) {
        return jwtConfig.generateToken(email);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
