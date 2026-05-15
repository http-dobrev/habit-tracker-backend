package com.konstantin.habittracker.business.logic.service;

import com.konstantin.habittracker.dto.request.UpdateHabitCompletionRequest;
import com.konstantin.habittracker.dto.response.DailyHabitResponse;
import com.konstantin.habittracker.exception.HabitNotFoundException;
import com.konstantin.habittracker.model.Habit;
import com.konstantin.habittracker.model.HabitCompletion;
import com.konstantin.habittracker.model.User;
import com.konstantin.habittracker.persistence.HabitCompletionRepository;
import com.konstantin.habittracker.persistence.HabitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DailyHabitService {

    private final HabitRepository habitRepository;
    private final HabitCompletionRepository habitCompletionRepository;
    private final AuthenticatedUserService authenticatedUserService;

    public DailyHabitService(
            HabitRepository habitRepository,
            HabitCompletionRepository habitCompletionRepository,
            AuthenticatedUserService authenticatedUserService
    ) {
        this.habitRepository = habitRepository;
        this.habitCompletionRepository = habitCompletionRepository;
        this.authenticatedUserService = authenticatedUserService;
    }

    @Transactional
    public List<DailyHabitResponse> getTodayHabits() {
        User user = authenticatedUserService.getAuthenticatedUser();
        LocalDate today = LocalDate.now();

        List<Habit> habits = habitRepository.findByUserId(user.getId());

        List<HabitCompletion> existingCompletions = habitCompletionRepository
                .findByHabitUserIdAndCompletionDate(user.getId(), today);

        Set<Long> existingHabitIds = existingCompletions.stream()
                .map(hc -> hc.getHabit().getId())
                .collect(Collectors.toSet());

        List<HabitCompletion> toCreate = habits.stream()
                .filter(h -> !existingHabitIds.contains(h.getId()))
                .map(h -> new HabitCompletion(h, today))
                .toList();

        if (!toCreate.isEmpty()) {
            habitCompletionRepository.saveAll(toCreate);
        }

        return habitCompletionRepository
                .findByHabitUserIdAndCompletionDate(user.getId(), today)
                .stream()
                .map(this::mapToDailyHabitResponse)
                .toList();
    }

    @Transactional
    public DailyHabitResponse updateTodayHabitCompletion(
            Long habitId,
            UpdateHabitCompletionRequest request
    ) {
        User user = authenticatedUserService.getAuthenticatedUser();
        LocalDate today = LocalDate.now();

        Habit habit = habitRepository.findByIdAndUserId(habitId, user.getId())
                .orElseThrow(() -> new HabitNotFoundException("Habit not found"));

        HabitCompletion habitCompletion = habitCompletionRepository
                .findByHabitIdAndHabitUserIdAndCompletionDate(habitId, user.getId(), today)
                .orElseGet(() -> new HabitCompletion(habit, today));

        habitCompletion.updateCompleted(request.getCompleted());

        HabitCompletion savedHabitCompletion = habitCompletionRepository.save(habitCompletion);
        return mapToDailyHabitResponse(savedHabitCompletion);
    }

    private DailyHabitResponse mapToDailyHabitResponse(HabitCompletion habitCompletion) {
        Habit habit = habitCompletion.getHabit();
        return new DailyHabitResponse(
                habit.getId(),
                habit.getName(),
                habit.getType(),
                habitCompletion.getCompletionDate(),
                habitCompletion.isCompleted()
        );
    }
}