package com.konstantin.habittracker.controller;

import com.konstantin.habittracker.business.logic.service.HabitCompletionService;
import com.konstantin.habittracker.business.logic.service.JwtService;
import com.konstantin.habittracker.configuration.SecurityConfig;
import com.konstantin.habittracker.dto.response.HabitCompletionResponse;
import com.konstantin.habittracker.model.HabitType;
import com.konstantin.habittracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HabitCompletionController.class)
@Import(SecurityConfig.class)
class HabitCompletionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HabitCompletionService habitCompletionService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void getAllHabitCompletions_returnsListOf200() throws Exception {
        HabitCompletionResponse response = new HabitCompletionResponse(
                1L, 1L, "Exercise", HabitType.good, LocalDate.now(), true
        );
        when(habitCompletionService.getAllHabitCompletions()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/habit-completions").with(user("user")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].habitId").value(1))
                .andExpect(jsonPath("$[0].habitName").value("Exercise"))
                .andExpect(jsonPath("$[0].completed").value(true));
    }

    @Test
    void getAllHabitCompletions_emptyList_returns200() throws Exception {
        when(habitCompletionService.getAllHabitCompletions()).thenReturn(List.of());

        mockMvc.perform(get("/api/habit-completions").with(user("user")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getAllHabitCompletions_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/habit-completions"))
                .andExpect(status().isUnauthorized());
    }
}
