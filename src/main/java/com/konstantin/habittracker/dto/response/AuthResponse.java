package com.konstantin.habittracker.dto.response;

public record AuthResponse (
        String token,
        int expiresIn,
        UserResponse user
) {}