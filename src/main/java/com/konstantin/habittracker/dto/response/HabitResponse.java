package com.konstantin.habittracker.dto.response;

import com.konstantin.habittracker.model.HabitType;

import java.time.LocalDateTime;

public class HabitResponse {
    private Long id;
    private String name;
    private HabitType type;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public HabitResponse(Long id, String name, HabitType type, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public HabitType getType() { return type; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}