package com.konstantin.habittracker.business.logic.service;

import com.konstantin.habittracker.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RegisterValidationServiceTest {

    private RegisterValidationService validationService;

    @BeforeEach
    void setUp() {
        validationService = new RegisterValidationService();
    }

    @Test
    void shouldReturnErrorWhenUserIsNull() {
        List<String> errors = validationService.validateUser(null);

        assertEquals(1, errors.size());
        assertTrue(errors.contains("User object cannot be null"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "invalidemail", "test.com"})
    void shouldReturnErrorForInvalidEmail(String email) {
        User user = new User();
        user.setName("Konstantin");
        user.setEmail(email);
        user.setPassword("password123");

        List<String> errors = validationService.validateUser(user);

        assertTrue(errors.contains("Invalid email format."));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "A", "Ko"})
    void shouldReturnErrorForInvalidUsername(String name) {
        User user = new User();
        user.setName(name);
        user.setEmail("test@email.com");
        user.setPassword("password123");

        List<String> errors = validationService.validateUser(user);

        assertTrue(errors.contains("Username must be at least 3 characters long."));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "1", "123", "12345"})
    void shouldReturnErrorForInvalidPassword(String password) {
        User user = new User();
        user.setName("Konstantin");
        user.setEmail("test@email.com");
        user.setPassword(password);

        List<String> errors = validationService.validateUser(user);

        assertTrue(errors.contains("Password must be at least 6 characters long."));
    }

    @Test
    void shouldReturnNoErrorsForValidUser() {
        User user = new User();
        user.setName("Konstantin");
        user.setEmail("test@email.com");
        user.setPassword("password123");

        List<String> errors = validationService.validateUser(user);

        assertTrue(errors.isEmpty());
    }
}