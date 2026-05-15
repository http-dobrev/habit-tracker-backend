package com.konstantin.habittracker.controller;

import com.konstantin.habittracker.business.logic.service.DailyHabitService;
import com.konstantin.habittracker.dto.request.UpdateHabitCompletionRequest;
import com.konstantin.habittracker.dto.response.DailyHabitResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/daily-habits")
public class DailyHabitController {

    private final DailyHabitService dailyHabitService;

    public DailyHabitController(DailyHabitService dailyHabitService) {
        this.dailyHabitService = dailyHabitService;
    }

    @GetMapping
    public ResponseEntity<List<DailyHabitResponse>> getTodayHabits() {
        return ResponseEntity.ok(dailyHabitService.getTodayHabits());
    }

    @PatchMapping("/{habitId}/completion")
    public ResponseEntity<DailyHabitResponse> updateTodayHabitCompletion(
            @PathVariable Long habitId,
            @Valid @RequestBody UpdateHabitCompletionRequest request
    ) {
        return ResponseEntity.ok(dailyHabitService.updateTodayHabitCompletion(habitId, request));
    }
}