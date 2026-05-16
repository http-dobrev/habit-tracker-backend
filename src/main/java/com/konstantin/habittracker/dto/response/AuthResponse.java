package com.konstantin.habittracker.dto.response;

public record AuthResponse (
        String token,
        String refreshToken,
        int expiresIn,
        UserResponse user
) {}