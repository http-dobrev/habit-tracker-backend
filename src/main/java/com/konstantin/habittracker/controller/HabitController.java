package com.konstantin.habittracker.controller;

import com.konstantin.habittracker.business.logic.service.HabitService;
import com.konstantin.habittracker.dto.request.CreateHabitRequest;
import com.konstantin.habittracker.dto.request.UpdateHabitRequest;
import com.konstantin.habittracker.dto.response.HabitResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/habits")
public class HabitController {

    private final HabitService habitService;

    public HabitController(HabitService habitService) {
        this.habitService = habitService;
    }

    @PostMapping
    public ResponseEntity<HabitResponse> createHabit(
            @Valid @RequestBody CreateHabitRequest request
    ) {
        HabitResponse response = habitService.createHabit(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<HabitResponse>> getAllHabits() {
        List<HabitResponse> habits = habitService.getAllHabits();

        return ResponseEntity
                .ok(habits);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HabitResponse> getHabitById(
            @PathVariable Long id
    ) {
        HabitResponse response = habitService.getHabitById(id);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HabitResponse> updateHabit(
            @PathVariable Long id,
            @Valid @RequestBody UpdateHabitRequest request
    ) {
        HabitResponse response = habitService.updateHabit(id, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHabit(
            @PathVariable Long id
    ) {
        habitService.deleteHabit(id);

        return ResponseEntity.noContent().build();
    }
}