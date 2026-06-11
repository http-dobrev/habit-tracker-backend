package com.konstantin.habittracker.controller;

import com.konstantin.habittracker.business.logic.service.DailyHabitService;
import com.konstantin.habittracker.business.logic.service.JwtService;
import com.konstantin.habittracker.configuration.SecurityConfig;
import com.konstantin.habittracker.dto.response.DailyHabitResponse;
import com.konstantin.habittracker.exception.HabitNotFoundException;
import com.konstantin.habittracker.model.HabitType;
import com.konstantin.habittracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DailyHabitController.class)
@Import(SecurityConfig.class)
class DailyHabitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DailyHabitService dailyHabitService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserRepository userRepository;

    private DailyHabitResponse dailyHabitResponse() {
        return new DailyHabitResponse(1L, "Exercise", HabitType.good, LocalDate.now(), false);
    }

    @Test
    void getTodayHabits_returnsListOf200() throws Exception {
        when(dailyHabitService.getTodayHabits()).thenReturn(List.of(dailyHabitResponse()));

        mockMvc.perform(get("/api/daily-habits").with(user("user")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].habitId").value(1))
                .andExpect(jsonPath("$[0].habitName").value("Exercise"))
                .andExpect(jsonPath("$[0].completed").value(false));
    }

    @Test
    void getTodayHabits_emptyList_returns200() throws Exception {
        when(dailyHabitService.getTodayHabits()).thenReturn(List.of());

        mockMvc.perform(get("/api/daily-habits").with(user("user")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void updateTodayHabitCompletion_validRequest_returns200() throws Exception {
        DailyHabitResponse completed = new DailyHabitResponse(1L, "Exercise", HabitType.good, LocalDate.now(), true);
        when(dailyHabitService.updateTodayHabitCompletion(eq(1L), any())).thenReturn(completed);

        mockMvc.perform(patch("/api/daily-habits/1/completion").with(user("user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "completed": true
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.habitId").value(1))
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    void updateTodayHabitCompletion_missingCompleted_returns400() throws Exception {
        mockMvc.perform(patch("/api/daily-habits/1/completion").with(user("user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateTodayHabitCompletion_nonExistentHabit_returns404() throws Exception {
        when(dailyHabitService.updateTodayHabitCompletion(eq(99L), any()))
                .thenThrow(new HabitNotFoundException("Habit not found"));

        mockMvc.perform(patch("/api/daily-habits/99/completion").with(user("user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "completed": true
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Habit not found"));
    }

    @Test
    void getTodayHabits_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/daily-habits"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateTodayHabitCompletion_unauthenticated_returns401() throws Exception {
        mockMvc.perform(patch("/api/daily-habits/1/completion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "completed": true
                            }
                            """))
                .andExpect(status().isUnauthorized());
    }
}
