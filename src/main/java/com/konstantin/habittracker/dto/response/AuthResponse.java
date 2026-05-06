package com.konstantin.habittracker.dto.response;

public class AuthResponse {
    public String token;
    public int expiresIn;
    public UserResponse user;

    public AuthResponse(String token, int expiresIn, UserResponse user) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.user = user;
    }

    public String getToken() {
        return token;
    }
    public int getExpiresIn() {
        return expiresIn;
    }

    public UserResponse getUser() {
        return user;
    }
}