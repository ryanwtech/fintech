package com.fintech.web;

import com.fintech.config.JwtConfig;
import com.fintech.dto.AuthRequest;
import com.fintech.dto.AuthResponse;
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

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest, 
                                           HttpServletResponse response) {
        AuthResponse authResponse = authService.authenticate(authRequest);
        
        if (authResponse.isSuccess()) {
            String token = jwtConfig.generateToken(authRequest.getUsername());
            
            Cookie cookie = new Cookie(jwtConfig.getCookieName(), token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge((int) (jwtConfig.getExpiration() / 1000));
            response.addCookie(cookie);
        }
        
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie(jwtConfig.getCookieName(), "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        
        return ResponseEntity.ok(new AuthResponse("Logged out successfully", null, true));
    }
}
