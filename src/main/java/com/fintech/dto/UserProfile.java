package com.fintech.dto;

import java.time.LocalDateTime;
import java.util.UUID;

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

    // Constructors
    public UserProfile() {}

    public UserProfile(UUID id, String email, String username, String firstName, String lastName, String role, Boolean isActive, LocalDateTime createdAt, Long accountCount, Long transactionCount, Long categoryCount, Long budgetCount) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.accountCount = accountCount;
        this.transactionCount = transactionCount;
        this.categoryCount = categoryCount;
        this.budgetCount = budgetCount;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getAccountCount() {
        return accountCount;
    }

    public void setAccountCount(Long accountCount) {
        this.accountCount = accountCount;
    }

    public Long getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(Long transactionCount) {
        this.transactionCount = transactionCount;
    }

    public Long getCategoryCount() {
        return categoryCount;
    }

    public void setCategoryCount(Long categoryCount) {
        this.categoryCount = categoryCount;
    }

    public Long getBudgetCount() {
        return budgetCount;
    }

    public void setBudgetCount(Long budgetCount) {
        this.budgetCount = budgetCount;
    }
}
