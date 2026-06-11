package com.konstantin.habittracker.controller;

import com.konstantin.habittracker.business.logic.service.HabitService;
import com.konstantin.habittracker.business.logic.service.JwtService;
import com.konstantin.habittracker.configuration.SecurityConfig;
import com.konstantin.habittracker.dto.response.HabitResponse;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HabitController.class)
@Import(SecurityConfig.class)
class HabitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HabitService habitService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserRepository userRepository;

    private HabitResponse habitResponse() {
        return new HabitResponse(1L, "Exercise", HabitType.good, LocalDateTime.now(), LocalDateTime.now());
    }

    // ==================== createHabit ====================

    @Test
    void createHabit_validRequest_returns201() throws Exception {
        when(habitService.createHabit(any())).thenReturn(habitResponse());

        mockMvc.perform(post("/api/habits").with(user("user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Exercise",
                                  "type": "good"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Exercise"))
                .andExpect(jsonPath("$.type").value("good"));
    }

    @Test
    void createHabit_missingName_returns400() throws Exception {
        mockMvc.perform(post("/api/habits").with(user("user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "type": "good"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createHabit_blankName_returns400() throws Exception {
        mockMvc.perform(post("/api/habits").with(user("user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "   ",
                                  "type": "good"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createHabit_missingType_returns400() throws Exception {
        mockMvc.perform(post("/api/habits").with(user("user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Exercise"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createHabit_nameTooLong_returns400() throws Exception {
        mockMvc.perform(post("/api/habits").with(user("user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "This name is way too long!",
                                  "type": "good"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createHabit_unauthenticated_returns401() throws Exception {
        mockMvc.perform(post("/api/habits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Exercise",
                                  "type": "good"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    // ==================== getAllHabits ====================

    @Test
    void getAllHabits_returnsListOf200() throws Exception {
        when(habitService.getAllHabits()).thenReturn(List.of(habitResponse()));

        mockMvc.perform(get("/api/habits").with(user("user")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Exercise"));
    }

    @Test
    void getAllHabits_emptyList_returns200() throws Exception {
        when(habitService.getAllHabits()).thenReturn(List.of());

        mockMvc.perform(get("/api/habits").with(user("user")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getAllHabits_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/habits"))
                .andExpect(status().isUnauthorized());
    }

    // ==================== getHabitById ====================

    @Test
    void getHabitById_existingId_returns200() throws Exception {
        when(habitService.getHabitById(1L)).thenReturn(habitResponse());

        mockMvc.perform(get("/api/habits/1").with(user("user")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Exercise"));
    }

    @Test
    void getHabitById_nonExistentId_returns404() throws Exception {
        when(habitService.getHabitById(99L))
                .thenThrow(new HabitNotFoundException("Habit not found"));

        mockMvc.perform(get("/api/habits/99").with(user("user")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Habit not found"));
    }

    @Test
    void getHabitById_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/habits/1"))
                .andExpect(status().isUnauthorized());
    }

    // ==================== updateHabit ====================

    @Test
    void updateHabit_validRequest_returns200() throws Exception {
        HabitResponse updated = new HabitResponse(1L, "Meditation", HabitType.good, LocalDateTime.now(), LocalDateTime.now());
        when(habitService.updateHabit(eq(1L), any())).thenReturn(updated);

        mockMvc.perform(put("/api/habits/1").with(user("user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Meditation",
                                  "type": "good"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Meditation"));
    }

    @Test
    void updateHabit_missingName_returns400() throws Exception {
        mockMvc.perform(put("/api/habits/1").with(user("user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "type": "good"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateHabit_blankName_returns400() throws Exception {
        mockMvc.perform(put("/api/habits/1").with(user("user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "   ",
                                  "type": "good"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateHabit_missingType_returns400() throws Exception {
        mockMvc.perform(put("/api/habits/1").with(user("user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Meditation"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateHabit_nonExistentId_returns404() throws Exception {
        when(habitService.updateHabit(eq(99L), any()))
                .thenThrow(new HabitNotFoundException("Habit not found"));

        mockMvc.perform(put("/api/habits/99").with(user("user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Meditation",
                                  "type": "good"
                                }
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateHabit_unauthenticated_returns401() throws Exception {
        mockMvc.perform(put("/api/habits/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Meditation",
                                  "type": "good"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    // ==================== deleteHabit ====================

    @Test
    void deleteHabit_existingId_returns204() throws Exception {
        doNothing().when(habitService).deleteHabit(1L);

        mockMvc.perform(delete("/api/habits/1").with(user("user")))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteHabit_nonExistentId_returns404() throws Exception {
        doThrow(new HabitNotFoundException("Habit not found")).when(habitService).deleteHabit(99L);

        mockMvc.perform(delete("/api/habits/99").with(user("user")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Habit not found"));
    }

    @Test
    void deleteHabit_unauthenticated_returns401() throws Exception {
        mockMvc.perform(delete("/api/habits/1"))
                .andExpect(status().isUnauthorized());
    }
}
