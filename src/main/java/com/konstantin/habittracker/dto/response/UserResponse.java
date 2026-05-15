package com.konstantin.habittracker.dto.response;

public record UserResponse (
        Long id,
        String name,
        String email,
        String role
) {}
