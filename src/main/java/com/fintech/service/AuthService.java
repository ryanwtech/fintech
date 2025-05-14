package com.fintech.service;

import com.fintech.config.JwtConfig;
import com.fintech.domain.User;
import com.fintech.dto.AuthRequest;
import com.fintech.dto.AuthResponse;
import com.fintech.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtConfig jwtConfig;

    public AuthResponse authenticate(AuthRequest authRequest) {
        Optional<User> userOpt = userRepository.findByUsername(authRequest.getUsername());
        
        if (userOpt.isPresent() && passwordEncoder.matches(authRequest.getPassword(), userOpt.get().getPassword())) {
            String token = jwtConfig.generateToken(authRequest.getUsername());
            return new AuthResponse("Authentication successful", authRequest.getUsername(), true);
        }
        
        return new AuthResponse("Invalid credentials", null, false);
    }

    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}
