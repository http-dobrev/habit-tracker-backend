package com.konstantin.habittracker.business.logic.service;

import com.konstantin.habittracker.dto.request.LoginRequest;
import com.konstantin.habittracker.dto.response.AuthResponse;
import com.konstantin.habittracker.exception.InvalidCredentialsException;
import com.konstantin.habittracker.model.Role;
import com.konstantin.habittracker.model.User;
import com.konstantin.habittracker.persistence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceLoginTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthService(jwtService, userRepository, passwordEncoder);
    }

    @Test
    void shouldLoginSuccessfully() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("test@email.com");
        request.setPassword("password123");

        User user = new User(
                "Konstantin",
                "test@email.com",
                "hashedPassword",
                Role.USER
        );

        when(userRepository.findByEmail("test@email.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("password123", "hashedPassword"))
                .thenReturn(true);

        when(jwtService.generateToken(user))
                .thenReturn("jwt-token");

        when(jwtService.getExpirationInSeconds())
                .thenReturn(3600);

        // Act
        AuthResponse response = authService.login(request);

        // Assert
        assertEquals("jwt-token", response.getToken());
        assertEquals(3600, response.getExpiresIn());
        assertEquals("User successfully logged in", response.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("notfound@email.com");
        request.setPassword("password123");

        when(userRepository.findByEmail("notfound@email.com"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsWrong() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("test@email.com");
        request.setPassword("wrongPassword");

        User user = new User(
                "Konstantin",
                "test@email.com",
                "hashedPassword",
                Role.USER
        );

        when(userRepository.findByEmail("test@email.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("wrongPassword", "hashedPassword"))
                .thenReturn(false);

        // Act & Assert
        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    }

    @Test
    void shouldTrimAndLowercaseEmail() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("  TEST@EMAIL.COM  ");
        request.setPassword("password123");

        User user = new User(
                "Konstantin",
                "test@email.com",
                "hashedPassword",
                Role.USER
        );

        when(userRepository.findByEmail("test@email.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("password123", "hashedPassword"))
                .thenReturn(true);

        when(jwtService.generateToken(user))
                .thenReturn("jwt-token");

        when(jwtService.getExpirationInSeconds())
                .thenReturn(3600);

        // Act
        AuthResponse response = authService.login(request);

        // Assert
        assertNotNull(response);
        verify(userRepository).findByEmail("test@email.com");
    }
}