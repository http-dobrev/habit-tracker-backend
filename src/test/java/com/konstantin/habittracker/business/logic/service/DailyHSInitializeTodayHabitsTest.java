package com.konstantin.habittracker.business.logic.service;

import com.konstantin.habittracker.model.Habit;
import com.konstantin.habittracker.model.HabitCompletion;
import com.konstantin.habittracker.model.HabitType;
import com.konstantin.habittracker.model.User;
import com.konstantin.habittracker.persistence.HabitCompletionRepository;
import com.konstantin.habittracker.persistence.HabitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DailyHSInitializeTodayHabitsTest {

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
    void shouldCreateCompletionsForHabitsThatHaveNone() {
        Habit habit1 = buildHabit(1L, "Morning Exercise");
        Habit habit2 = buildHabit(2L, "Drink Water");

        when(habitRepository.findByUserId(user.getId())).thenReturn(List.of(habit1, habit2));
        when(habitCompletionRepository.findByHabitUserIdAndCompletionDate(user.getId(), today))
                .thenReturn(List.of());

        dailyHabitService.initializeTodayHabits();

        ArgumentCaptor<List<HabitCompletion>> captor = ArgumentCaptor.forClass(List.class);
        verify(habitCompletionRepository).saveAll(captor.capture());

        List<HabitCompletion> saved = captor.getValue();
        assertEquals(2, saved.size());
        assertEquals(habit1, saved.get(0).getHabit());
        assertEquals(habit2, saved.get(1).getHabit());
    }

    @Test
    void shouldSkipHabitsThatAlreadyHaveCompletionsToday() {
        Habit habit1 = buildHabit(1L, "Morning Exercise");
        Habit habit2 = buildHabit(2L, "Drink Water");

        HabitCompletion existingCompletion = new HabitCompletion(habit1, today);

        when(habitRepository.findByUserId(user.getId())).thenReturn(List.of(habit1, habit2));
        when(habitCompletionRepository.findByHabitUserIdAndCompletionDate(user.getId(), today))
                .thenReturn(List.of(existingCompletion));

        dailyHabitService.initializeTodayHabits();

        ArgumentCaptor<List<HabitCompletion>> captor = ArgumentCaptor.forClass(List.class);
        verify(habitCompletionRepository).saveAll(captor.capture());

        List<HabitCompletion> saved = captor.getValue();
        assertEquals(1, saved.size());
        assertEquals(habit2, saved.get(0).getHabit());
    }

    @Test
    void shouldSaveNothingWhenAllHabitsAlreadyInitialized() {
        Habit habit = buildHabit(1L, "Morning Exercise");
        HabitCompletion existingCompletion = new HabitCompletion(habit, today);

        when(habitRepository.findByUserId(user.getId())).thenReturn(List.of(habit));
        when(habitCompletionRepository.findByHabitUserIdAndCompletionDate(user.getId(), today))
                .thenReturn(List.of(existingCompletion));

        dailyHabitService.initializeTodayHabits();

        ArgumentCaptor<List<HabitCompletion>> captor = ArgumentCaptor.forClass(List.class);
        verify(habitCompletionRepository).saveAll(captor.capture());

        assertEquals(0, captor.getValue().size());
    }

    @Test
    void shouldSaveNothingWhenUserHasNoHabits() {
        when(habitRepository.findByUserId(user.getId())).thenReturn(List.of());
        when(habitCompletionRepository.findByHabitUserIdAndCompletionDate(user.getId(), today))
                .thenReturn(List.of());

        dailyHabitService.initializeTodayHabits();

        ArgumentCaptor<List<HabitCompletion>> captor = ArgumentCaptor.forClass(List.class);
        verify(habitCompletionRepository).saveAll(captor.capture());

        assertEquals(0, captor.getValue().size());
    }

    private Habit buildHabit(Long id, String name) {
        Habit habit = new Habit();
        habit.setId(id);
        habit.setName(name);
        habit.setType(HabitType.good);
        habit.setUser(user);
        return habit;
    }
}