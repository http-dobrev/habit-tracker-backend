package com.konstantin.habittracker.business.logic.service;

import com.konstantin.habittracker.dto.response.HabitCompletionResponse;
import com.konstantin.habittracker.model.Habit;
import com.konstantin.habittracker.model.HabitCompletion;
import com.konstantin.habittracker.model.HabitType;
import com.konstantin.habittracker.model.User;
import com.konstantin.habittracker.repository.HabitCompletionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HabitCompletionServiceGetAllHabitCompletionsTest {

    @Mock
    private HabitCompletionRepository habitCompletionRepository;

    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @InjectMocks
    private HabitCompletionService habitCompletionService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user);
    }

    @Test
    void shouldReturnAllCompletionsForAuthenticatedUser() {
        Habit habit = buildHabit(1L, "Morning Exercise", HabitType.good);
        HabitCompletion completion = buildCompletion(1L, habit, LocalDate.now(), true);

        when(habitCompletionRepository.findByHabitUserId(user.getId()))
                .thenReturn(List.of(completion));

        List<HabitCompletionResponse> result = habitCompletionService.getAllHabitCompletions();

        assertEquals(1, result.size());
        HabitCompletionResponse response = result.get(0);
        assertEquals(1L, response.id());
        assertEquals(1L, response.habitId());
        assertEquals("Morning Exercise", response.habitName());
        assertEquals(HabitType.good, response.habitType());
        assertEquals(LocalDate.now(), response.completionDate());
        assertTrue(response.completed());
    }

    @Test
    void shouldReturnEmptyListWhenNoCompletionsExist() {
        when(habitCompletionRepository.findByHabitUserId(user.getId()))
                .thenReturn(List.of());

        List<HabitCompletionResponse> result = habitCompletionService.getAllHabitCompletions();

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnMultipleCompletionsAcrossDifferentDates() {
        Habit habit = buildHabit(1L, "Drink Water", HabitType.good);
        HabitCompletion first = buildCompletion(1L, habit, LocalDate.now().minusDays(1), true);
        HabitCompletion second = buildCompletion(2L, habit, LocalDate.now(), false);

        when(habitCompletionRepository.findByHabitUserId(user.getId()))
                .thenReturn(List.of(first, second));

        List<HabitCompletionResponse> result = habitCompletionService.getAllHabitCompletions();

        assertEquals(2, result.size());
        assertTrue(result.get(0).completed());
        assertFalse(result.get(1).completed());
    }

    private Habit buildHabit(Long id, String name, HabitType type) {
        Habit habit = new Habit();
        habit.setId(id);
        habit.setName(name);
        habit.setType(type);
        habit.setUser(user);
        return habit;
    }

    private HabitCompletion buildCompletion(Long id, Habit habit, LocalDate date, boolean completed) {
        HabitCompletion completion = new HabitCompletion(habit, date);
        completion.setId(id);
        completion.setCompleted(completed);
        completion.setCreatedAt(LocalDateTime.now());
        return completion;
    }
}