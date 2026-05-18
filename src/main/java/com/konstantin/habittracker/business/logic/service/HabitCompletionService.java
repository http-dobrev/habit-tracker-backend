package com.konstantin.habittracker.business.logic.service;

import com.konstantin.habittracker.dto.response.HabitCompletionResponse;
import com.konstantin.habittracker.model.Habit;
import com.konstantin.habittracker.model.HabitCompletion;
import com.konstantin.habittracker.model.User;
import com.konstantin.habittracker.repository.HabitCompletionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HabitCompletionService {

    private final HabitCompletionRepository habitCompletionRepository;
    private final AuthenticatedUserService authenticatedUserService;

    public HabitCompletionService(HabitCompletionRepository habitCompletionRepository, AuthenticatedUserService authenticatedUserService) {
        this.habitCompletionRepository = habitCompletionRepository;
        this.authenticatedUserService = authenticatedUserService;
    }

    @Transactional
    public List<HabitCompletionResponse> getAllHabitCompletions(){
        User user = authenticatedUserService.getAuthenticatedUser();

        return habitCompletionRepository.findByHabitUserId(user.getId())
                .stream()
                .map(this::mapToHabitCompletionResponse)
                .toList();
    }

    private HabitCompletionResponse mapToHabitCompletionResponse(HabitCompletion habitCompletion) {
        Habit habit = habitCompletion.getHabit();
        return new HabitCompletionResponse(
                habitCompletion.getId(),
                habit.getId(),
                habit.getName(),
                habit.getType(),
                habitCompletion.getCompletionDate(),
                habitCompletion.isCompleted(),
                habitCompletion.getCreatedAt()
        );
    }
}
