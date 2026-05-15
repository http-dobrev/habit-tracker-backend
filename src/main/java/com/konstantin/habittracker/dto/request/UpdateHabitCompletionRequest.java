package com.konstantin.habittracker.dto.request;

import jakarta.validation.constraints.NotNull;

public class UpdateHabitCompletionRequest {

    @NotNull(message = "Completed value is required")
    private Boolean completed;

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }
}