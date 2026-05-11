package com.konstantin.habittracker.dto.request;

import com.konstantin.habittracker.model.HabitType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UpdateHabitRequest {

    @NotBlank(message = "Habit name is required")
    @Size(max = 255, message = "Habit name cannot be longer than 255 characters")
    private String name;

    @NotNull(message = "Habit type is required")
    private HabitType type;

    public UpdateHabitRequest() {}

    public UpdateHabitRequest(String name, HabitType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public HabitType getType() {
        return type;
    }
}