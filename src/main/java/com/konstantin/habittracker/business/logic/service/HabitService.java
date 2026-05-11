package com.konstantin.habittracker.business.logic.service;

import com.konstantin.habittracker.dto.request.CreateHabitRequest;
import com.konstantin.habittracker.dto.request.UpdateHabitRequest;
import com.konstantin.habittracker.dto.response.HabitResponse;
import com.konstantin.habittracker.exception.HabitNotFoundException;
import com.konstantin.habittracker.exception.InvalidHabitDataException;
import com.konstantin.habittracker.model.Habit;
import com.konstantin.habittracker.model.User;
import com.konstantin.habittracker.persistence.HabitRepository;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class HabitService {

    private final HabitRepository habitRepository;
    private final AuthenticatedUserService authenticatedUserService;

    public HabitService(HabitRepository habitRepository, AuthenticatedUserService authenticatedUserService) {
        this.habitRepository = habitRepository;
        this.authenticatedUserService = authenticatedUserService;
    }

    public HabitResponse createHabit(CreateHabitRequest request) {
        User user = authenticatedUserService.getAuthenticatedUser();

        String normalizedRequestName = request.getName().trim();

        if (habitRepository.existsByUserIdAndNameIgnoreCase(user.getId(), normalizedRequestName)) {
            throw new InvalidHabitDataException("Habit with this name already exists");
        }

        Habit habit = new Habit(
                user,
                normalizedRequestName,
                request.getType()
        );

        Habit savedHabit = habitRepository.save(habit);

        return new HabitResponse(
                savedHabit.getId(),
                savedHabit.getName(),
                savedHabit.getType(),
                savedHabit.getCreatedAt(),
                savedHabit.getUpdatedAt()
        );
    }

    public List<HabitResponse> getAllHabits() {
        User user = authenticatedUserService.getAuthenticatedUser();

        return habitRepository.findByUserId(user.getId())
                .stream()
                .map(habit -> new HabitResponse(
                        habit.getId(),
                        habit.getName(),
                        habit.getType(),
                        habit.getCreatedAt(),
                        habit.getUpdatedAt()
                ))
                .toList();
    }

    public HabitResponse getHabitById(Long habitId) {
        User user = authenticatedUserService.getAuthenticatedUser();

        Habit habit = habitRepository.findByIdAndUserId(habitId, user.getId())
                .orElseThrow(() -> new HabitNotFoundException("Habit not found"));

        return new HabitResponse(
                habit.getId(),
                habit.getName(),
                habit.getType(),
                habit.getCreatedAt(),
                habit.getUpdatedAt()
        );
    }

    public HabitResponse updateHabit(Long habitId, UpdateHabitRequest request) {
        User user = authenticatedUserService.getAuthenticatedUser();

        Habit habit = habitRepository.findByIdAndUserId(habitId, user.getId())
                .orElseThrow(() -> new HabitNotFoundException("Habit not found"));

        String normalizedRequestName = request.getName().trim();

        boolean nameChanged = !habit.getName().equalsIgnoreCase(normalizedRequestName);

        if (nameChanged && habitRepository.existsByUserIdAndNameIgnoreCase(user.getId(), normalizedRequestName)) {
            throw new InvalidHabitDataException("Habit with this name already exists");
        }

        habit.updateName(normalizedRequestName);
        habit.updateType(request.getType());

        Habit updatedHabit = habitRepository.save(habit);

        return new HabitResponse(
                updatedHabit.getId(),
                updatedHabit.getName(),
                updatedHabit.getType(),
                updatedHabit.getCreatedAt(),
                updatedHabit.getUpdatedAt()
        );
    }

    public void deleteHabit(Long habitId) {
        User user = authenticatedUserService.getAuthenticatedUser();

        Habit habit = habitRepository.findByIdAndUserId(habitId, user.getId())
                .orElseThrow(() -> new HabitNotFoundException("Habit not found"));

        habitRepository.delete(habit);
    }
}