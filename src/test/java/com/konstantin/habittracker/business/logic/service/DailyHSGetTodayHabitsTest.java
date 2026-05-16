package com.konstantin.habittracker.business.logic.service;

import com.konstantin.habittracker.dto.response.DailyHabitResponse;
import com.konstantin.habittracker.model.Habit;
import com.konstantin.habittracker.model.HabitCompletion;
import com.konstantin.habittracker.model.HabitType;
import com.konstantin.habittracker.model.User;
import com.konstantin.habittracker.repository.HabitCompletionRepository;
import com.konstantin.habittracker.repository.HabitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DailyHSGetTodayHabitsTest {

    @Mock
    private HabitRepository habitRepository;

    @Mock
    private HabitCompletionRepository habitCompletionRepository;

    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @InjectMocks
    private DailyHabitService dailyHabitService;

    private User user;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        today = LocalDate.now();

        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user);
    }

    @Test
    void shouldReturnMappedResponsesForTodayCompletions() {
        Habit habit = buildHabit(1L, "Morning Exercise", HabitType.good);
        HabitCompletion completion = new HabitCompletion(habit, today);

        when(habitCompletionRepository.findByHabitUserIdAndCompletionDate(user.getId(), today))
                .thenReturn(List.of(completion));

        List<DailyHabitResponse> result = dailyHabitService.getTodayHabits();

        assertEquals(1, result.size());
        DailyHabitResponse response = result.get(0);
        assertEquals(1L, response.habitId());
        assertEquals("Morning Exercise", response.habitName());
        assertEquals(HabitType.good, response.habitType());
        assertEquals(today, response.completionDate());
        assertFalse(response.completed());
    }

    @Test
    void shouldReturnEmptyListWhenNoCompletionsExist() {
        when(habitCompletionRepository.findByHabitUserIdAndCompletionDate(user.getId(), today))
                .thenReturn(List.of());

        List<DailyHabitResponse> result = dailyHabitService.getTodayHabits();

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnMultipleHabitsWithCorrectCompletionState() {
        Habit goodHabit = buildHabit(1L, "Drink Water", HabitType.good);
        Habit badHabit = buildHabit(2L, "Smoking", HabitType.bad);

        HabitCompletion completedOne = new HabitCompletion(goodHabit, today);
        completedOne.updateCompleted(true);
        HabitCompletion notCompletedOne = new HabitCompletion(badHabit, today);

        when(habitCompletionRepository.findByHabitUserIdAndCompletionDate(user.getId(), today))
                .thenReturn(List.of(completedOne, notCompletedOne));

        List<DailyHabitResponse> result = dailyHabitService.getTodayHabits();

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
}

