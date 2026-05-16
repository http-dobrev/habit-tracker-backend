package com.konstantin.habittracker.business.logic.service;

import com.konstantin.habittracker.dto.request.CreateHabitRequest;
import com.konstantin.habittracker.dto.response.HabitResponse;
import com.konstantin.habittracker.exception.InvalidHabitDataException;
import com.konstantin.habittracker.model.Habit;
import com.konstantin.habittracker.model.HabitType;
import com.konstantin.habittracker.model.User;
import com.konstantin.habittracker.repository.HabitRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HabitServiceCreateTest {

    @Mock private HabitRepository habitRepository;
    @Mock private AuthenticatedUserService authenticatedUserService;

    @InjectMocks private HabitService habitService;

    @Test
    void createHabit_ShouldCreateHabit_WhenHabitNameDoesNotExist() {
        User user = mock(User.class);
        CreateHabitRequest request = mock(CreateHabitRequest.class);
        Habit savedHabit = mock(Habit.class);

        when(user.getId()).thenReturn(1L);
        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user);

        when(request.getName()).thenReturn("  Gym  ");
        when(request.getType()).thenReturn(HabitType.good);

        when(habitRepository.existsByUserIdAndNameIgnoreCase(1L, "Gym")).thenReturn(false);
        when(habitRepository.save(any(Habit.class))).thenReturn(savedHabit);

        when(savedHabit.getId()).thenReturn(10L);
        when(savedHabit.getName()).thenReturn("Gym");
        when(savedHabit.getType()).thenReturn(HabitType.good);

        HabitResponse response = habitService.createHabit(request);

        assertEquals(10L, response.id());
        assertEquals("Gym", response.name());
        assertEquals(HabitType.good, response.type());

        ArgumentCaptor<Habit> habitCaptor = ArgumentCaptor.forClass(Habit.class);
        verify(habitRepository).save(habitCaptor.capture());

        Habit capturedHabit = habitCaptor.getValue();

        assertEquals("Gym", capturedHabit.getName());
        assertEquals(HabitType.good, capturedHabit.getType());
    }

    @Test
    void createHabit_ShouldThrowInvalidHabitDataException_WhenHabitNameAlreadyExists() {
        User user = mock(User.class);
        CreateHabitRequest request = mock(CreateHabitRequest.class);

        when(user.getId()).thenReturn(1L);
        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user);

        when(request.getName()).thenReturn("Gym");
        when(habitRepository.existsByUserIdAndNameIgnoreCase(1L, "Gym")).thenReturn(true);

        InvalidHabitDataException exception = assertThrows(
                InvalidHabitDataException.class,
                () -> habitService.createHabit(request)
        );

        assertEquals("Habit with this name already exists", exception.getMessage());

        verify(habitRepository, never()).save(any(Habit.class));
    }
}