package com.konstantin.habittracker.controller;

import com.konstantin.habittracker.business.logic.service.HabitCompletionService;
import com.konstantin.habittracker.dto.response.HabitCompletionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/completions")
public class HabitCompletionController {

    private final HabitCompletionService habitCompletionService;

    public HabitCompletionController(HabitCompletionService habitCompletionService) {
        this.habitCompletionService = habitCompletionService;
    }

    @GetMapping("/completions")
    public ResponseEntity<List<HabitCompletionResponse>> getAllHabitCompletions() {
        return ResponseEntity.ok(habitCompletionService.getAllHabitCompletions());
    }
}
