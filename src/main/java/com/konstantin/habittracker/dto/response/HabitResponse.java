package com.konstantin.habittracker.dto.response;

import com.konstantin.habittracker.model.HabitType;

import java.time.LocalDateTime;

public record HabitResponse(
        Long id,
        String name,
        HabitType type,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}