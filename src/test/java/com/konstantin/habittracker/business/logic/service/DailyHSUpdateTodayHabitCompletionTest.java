package com.konstantin.habittracker.business.logic.service;

import com.konstantin.habittracker.dto.request.UpdateHabitCompletionRequest;
import com.konstantin.habittracker.dto.response.DailyHabitResponse;
import com.konstantin.habittracker.exception.HabitNotFoundException;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DailyHSUpdateTodayHabitCompletionTest {

    @Mock
    private HabitRepository habitRepository;

    @Mock
    private HabitCompletionRepository habitCompletionRepository;

    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @InjectMocks
    private DailyHabitService dailyHabitService;

    private User user;
    private Habit habit;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        today = LocalDate.now();

        habit = new Habit();
        habit.setId(10L);
        habit.setName("Morning Exercise");
        habit.setType(HabitType.good);
        habit.setUser(user);

        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user);
    }

    @Test
    void shouldMarkExistingCompletionAsCompleted() {
        HabitCompletion existingCompletion = new HabitCompletion(habit, today);
        UpdateHabitCompletionRequest request = buildRequest(true);

        when(habitRepository.findByIdAndUserId(habit.getId(), user.getId()))
                .thenReturn(Optional.of(habit));
        when(habitCompletionRepository.findByHabitIdAndHabitUserIdAndCompletionDate(
                habit.getId(), user.getId(), today))
                .thenReturn(Optional.of(existingCompletion));
        when(habitCompletionRepository.save(existingCompletion)).thenReturn(existingCompletion);

        DailyHabitResponse result = dailyHabitService.updateTodayHabitCompletion(habit.getId(), request);

        assertTrue(result.completed());
        verify(habitCompletionRepository).save(existingCompletion);
    }

    @Test
    void shouldMarkExistingCompletionAsNotCompleted() {
        HabitCompletion existingCompletion = new HabitCompletion(habit, today);
        existingCompletion.setCompleted(true);
        UpdateHabitCompletionRequest request = buildRequest(false);

        when(habitRepository.findByIdAndUserId(habit.getId(), user.getId()))
                .thenReturn(Optional.of(habit));
        when(habitCompletionRepository.findByHabitIdAndHabitUserIdAndCompletionDate(
                habit.getId(), user.getId(), today))
                .thenReturn(Optional.of(existingCompletion));
        when(habitCompletionRepository.save(existingCompletion)).thenReturn(existingCompletion);

        DailyHabitResponse result = dailyHabitService.updateTodayHabitCompletion(habit.getId(), request);

        assertFalse(result.completed());
    }

    @Test
    void shouldCreateNewCompletionIfNoneExistsForToday() {
        UpdateHabitCompletionRequest request = buildRequest(true);

        when(habitRepository.findByIdAndUserId(habit.getId(), user.getId()))
                .thenReturn(Optional.of(habit));
        when(habitCompletionRepository.findByHabitIdAndHabitUserIdAndCompletionDate(
                habit.getId(), user.getId(), today))
                .thenReturn(Optional.empty());
        when(habitCompletionRepository.save(any(HabitCompletion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        DailyHabitResponse result = dailyHabitService.updateTodayHabitCompletion(habit.getId(), request);

        assertTrue(result.completed());
        verify(habitCompletionRepository).save(any(HabitCompletion.class));
    }

    @Test
    void shouldThrowWhenHabitDoesNotBelongToUser() {
        UpdateHabitCompletionRequest request = buildRequest(true);

        when(habitRepository.findByIdAndUserId(habit.getId(), user.getId()))
                .thenReturn(Optional.empty());

        assertThrows(HabitNotFoundException.class,
                () -> dailyHabitService.updateTodayHabitCompletion(habit.getId(), request));

        verify(habitCompletionRepository, never()).save(any());
    }

    @Test
    void shouldReturnCorrectHabitMetadataInResponse() {
        HabitCompletion existingCompletion = new HabitCompletion(habit, today);
        UpdateHabitCompletionRequest request = buildRequest(true);

        when(habitRepository.findByIdAndUserId(habit.getId(), user.getId()))
                .thenReturn(Optional.of(habit));
        when(habitCompletionRepository.findByHabitIdAndHabitUserIdAndCompletionDate(
                habit.getId(), user.getId(), today))
                .thenReturn(Optional.of(existingCompletion));
        when(habitCompletionRepository.save(existingCompletion)).thenReturn(existingCompletion);

        DailyHabitResponse result = dailyHabitService.updateTodayHabitCompletion(habit.getId(), request);

        assertEquals(habit.getId(), result.habitId());
        assertEquals("Morning Exercise", result.habitName());
        assertEquals(HabitType.good, result.habitType());
        assertEquals(today, result.completionDate());
    }

    private UpdateHabitCompletionRequest buildRequest(boolean completed) {
        UpdateHabitCompletionRequest request = new UpdateHabitCompletionRequest();
        request.setCompleted(completed);
        return request;
    }
}