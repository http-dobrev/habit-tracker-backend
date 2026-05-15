package com.konstantin.habittracker.business.logic.service;

import com.konstantin.habittracker.dto.response.HabitResponse;
import com.konstantin.habittracker.exception.HabitNotFoundException;
import com.konstantin.habittracker.model.Habit;
import com.konstantin.habittracker.model.HabitType;
import com.konstantin.habittracker.model.User;
import com.konstantin.habittracker.persistence.HabitRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HabitServiceGetByIdTest {

    @Mock private HabitRepository habitRepository;
    @Mock private AuthenticatedUserService authenticatedUserService;

    @InjectMocks private HabitService habitService;

    @Test
    void getHabitById_ShouldReturnHabit_WhenHabitExistsForUser() {
        User user = mock(User.class);
        Habit habit = mock(Habit.class);

        when(user.getId()).thenReturn(1L);
        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user);

        when(habitRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(habit));

        when(habit.getId()).thenReturn(10L);
        when(habit.getName()).thenReturn("Gym");
        when(habit.getType()).thenReturn(HabitType.good);

        HabitResponse response = habitService.getHabitById(10L);

        assertEquals(10L, response.id());
        assertEquals("Gym", response.name());
        assertEquals(HabitType.good, response.type());
    }

    @Test
    void getHabitById_ShouldThrowHabitNotFoundException_WhenHabitDoesNotExist() {
        User user = mock(User.class);

        when(user.getId()).thenReturn(1L);
        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user);

        when(habitRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

        HabitNotFoundException exception = assertThrows(
                HabitNotFoundException.class,
                () -> habitService.getHabitById(99L)
        );

        assertEquals("Habit not found", exception.getMessage());
    }
}