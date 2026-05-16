package com.konstantin.habittracker.dto.response;

public record RefreshResponse (
        String token,
        Integer expiresIn
) {}