package com.fintech.web;

import com.fintech.config.JwtConfig;
import com.fintech.dto.*;
import com.fintech.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtConfig jwtConfig;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest,
                                               HttpServletResponse response) {
        try {
            AuthResponse authResponse = authService.register(registerRequest);
            
            if (authResponse.isSuccess()) {
                String token = jwtConfig.generateToken(registerRequest.getEmail());
                setJwtCookie(response, token);
            }
            
            return ResponseEntity.ok(authResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(e.getMessage(), null, false));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest, 
                                           HttpServletResponse response) {
        try {
            AuthResponse authResponse = authService.login(loginRequest);
            
            if (authResponse.isSuccess()) {
                String token = jwtConfig.generateToken(loginRequest.getEmail());
                setJwtCookie(response, token);
            }
            
            return ResponseEntity.ok(authResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(e.getMessage(), null, false));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(HttpServletResponse response) {
        clearJwtCookie(response);
        return ResponseEntity.ok(new AuthResponse("Logged out successfully", null, true));
    }

    private void setJwtCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(jwtConfig.getCookieName(), token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) (jwtConfig.getExpiration() / 1000));
        response.addCookie(cookie);
    }

    private void clearJwtCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(jwtConfig.getCookieName(), "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
