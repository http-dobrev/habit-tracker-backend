package com.konstantin.habittracker.business.logic.service;

import com.konstantin.habittracker.dto.response.HabitResponse;
import com.konstantin.habittracker.model.Habit;
import com.konstantin.habittracker.model.HabitType;
import com.konstantin.habittracker.model.User;
import com.konstantin.habittracker.persistence.HabitRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HabitServiceGetAllTest {

    @Mock private HabitRepository habitRepository;
    @Mock private AuthenticatedUserService authenticatedUserService;

    @InjectMocks private HabitService habitService;

    @Test
    void getAllHabits_ShouldReturnAllHabitsForAuthenticatedUser() {
        User user = mock(User.class);
        Habit habit1 = mock(Habit.class);
        Habit habit2 = mock(Habit.class);

        when(user.getId()).thenReturn(1L);
        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user);

        when(habit1.getId()).thenReturn(10L);
        when(habit1.getName()).thenReturn("Gym");
        when(habit1.getType()).thenReturn(HabitType.good);

        when(habit2.getId()).thenReturn(11L);
        when(habit2.getName()).thenReturn("Smoking");
        when(habit2.getType()).thenReturn(HabitType.bad);

        when(habitRepository.findByUserId(1L)).thenReturn(List.of(habit1, habit2));

        List<HabitResponse> responses = habitService.getAllHabits();

        assertEquals(2, responses.size());

        assertEquals(10L, responses.get(0).getId());
        assertEquals("Gym", responses.get(0).getName());
        assertEquals(HabitType.good, responses.get(0).getType());

        assertEquals(11L, responses.get(1).getId());
        assertEquals("Smoking", responses.get(1).getName());
        assertEquals(HabitType.bad, responses.get(1).getType());

        verify(habitRepository).findByUserId(1L);
    }
}