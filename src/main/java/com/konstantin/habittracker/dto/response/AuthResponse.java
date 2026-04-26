package com.konstantin.habittracker.dto.response;

public class AuthResponse {
    public String token;
    public int expiresIn;
    public String message;

    public AuthResponse(String token, int expiresIn, String message) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.message = message;
    }

    public String getToken() {
        return token;
    }
    public int getExpiresIn() {
        return expiresIn;
    }

    public String getMessage() {
        return message;
    }
}