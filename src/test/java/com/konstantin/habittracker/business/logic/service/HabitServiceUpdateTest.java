package com.konstantin.habittracker.business.logic.service;

import com.konstantin.habittracker.dto.request.UpdateHabitRequest;
import com.konstantin.habittracker.dto.response.HabitResponse;
import com.konstantin.habittracker.exception.HabitNotFoundException;
import com.konstantin.habittracker.exception.InvalidHabitDataException;
import com.konstantin.habittracker.model.Habit;
import com.konstantin.habittracker.model.HabitType;
import com.konstantin.habittracker.model.User;
import com.konstantin.habittracker.repository.HabitRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HabitServiceUpdateTest {

    @Mock private HabitRepository habitRepository;
    @Mock private AuthenticatedUserService authenticatedUserService;

    @InjectMocks private HabitService habitService;

    @Test
    void updateHabit_ShouldUpdateHabit_WhenHabitExistsAndNameIsUnique() {
        User user = mock(User.class);
        Habit habit = mock(Habit.class);
        UpdateHabitRequest request = mock(UpdateHabitRequest.class);

        when(user.getId()).thenReturn(1L);
        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user);

        when(habitRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(habit));

        when(habit.getName()).thenReturn("Old Gym", "New Gym");
        when(request.getName()).thenReturn("  New Gym  ");
        when(request.getType()).thenReturn(HabitType.good);

        when(habitRepository.existsByUserIdAndNameIgnoreCase(1L, "New Gym")).thenReturn(false);
        when(habitRepository.save(habit)).thenReturn(habit);

        when(habit.getId()).thenReturn(10L);
        when(habit.getType()).thenReturn(HabitType.good);

        HabitResponse response = habitService.updateHabit(10L, request);

        assertEquals(10L, response.id());
        assertEquals("New Gym", response.name());
        assertEquals(HabitType.good, response.type());

        verify(habit).setName("New Gym");
        verify(habit).setType(HabitType.good);
        verify(habitRepository).save(habit);
    }

    @Test
    void updateHabit_ShouldThrowHabitNotFoundException_WhenHabitDoesNotExist() {
        User user = mock(User.class);
        UpdateHabitRequest request = mock(UpdateHabitRequest.class);

        when(user.getId()).thenReturn(1L);
        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user);

        when(habitRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

        HabitNotFoundException exception = assertThrows(
                HabitNotFoundException.class,
                () -> habitService.updateHabit(99L, request)
        );

        assertEquals("Habit not found", exception.getMessage());

        verify(habitRepository, never()).save(any(Habit.class));
    }

    @Test
    void updateHabit_ShouldThrowInvalidHabitDataException_WhenNewNameAlreadyExists() {
        User user = mock(User.class);
        Habit habit = mock(Habit.class);
        UpdateHabitRequest request = mock(UpdateHabitRequest.class);

        when(user.getId()).thenReturn(1L);
        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user);

        when(habitRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(habit));

        when(habit.getName()).thenReturn("Gym");
        when(request.getName()).thenReturn("Reading");

        when(habitRepository.existsByUserIdAndNameIgnoreCase(1L, "Reading")).thenReturn(true);

        InvalidHabitDataException exception = assertThrows(
                InvalidHabitDataException.class,
                () -> habitService.updateHabit(10L, request)
        );

        assertEquals("Habit with this name already exists", exception.getMessage());

        verify(habitRepository, never()).save(any(Habit.class));
    }
}