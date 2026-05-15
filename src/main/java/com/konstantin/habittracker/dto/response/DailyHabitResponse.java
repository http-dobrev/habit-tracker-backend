package com.konstantin.habittracker.dto.response;

import com.konstantin.habittracker.model.HabitType;

import java.time.LocalDate;

public record DailyHabitResponse(
        Long habitId,
        String habitName,
        HabitType habitType,
        LocalDate completionDate,
        boolean completed
) {}