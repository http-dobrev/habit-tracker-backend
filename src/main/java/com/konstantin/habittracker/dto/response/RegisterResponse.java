package com.konstantin.habittracker.dto.response;

public class RegisterResponse {
    public String token;
    public int expiresIn;
    public String message;

    public RegisterResponse(String token, int expiresIn, String message) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.message = message;
    }
}