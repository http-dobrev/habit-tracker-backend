package com.konstantin.habittracker.dto.response;

import com.konstantin.habittracker.model.HabitType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record HabitCompletionResponse(
        Long id,
        Long habitId,
        String habitName,
        HabitType habitType,
        LocalDate completionDate,
        boolean completed,
        LocalDateTime createdAt
) {}