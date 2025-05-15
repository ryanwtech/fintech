package com.fintech.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {

    private UUID id;
    private String email;
    private String username;
    private String firstName;
    private String lastName;
    private String role;
    private Boolean isActive;
    private LocalDateTime createdAt;
    
    // Counts
    private Long accountCount;
    private Long transactionCount;
    private Long categoryCount;
    private Long budgetCount;
}
