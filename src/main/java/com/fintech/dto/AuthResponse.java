package com.fintech.dto;

public class AuthResponse {

    private String message;
    private UserProfile profile;
    private boolean success;

    // Constructors
    public AuthResponse() {}

    public AuthResponse(String message, UserProfile profile, boolean success) {
        this.message = message;
        this.profile = profile;
        this.success = success;
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserProfile getProfile() {
        return profile;
    }

    public void setProfile(UserProfile profile) {
        this.profile = profile;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
