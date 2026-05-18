package com.konstantin.habittracker.business.logic.service;

import com.konstantin.habittracker.dto.request.ChangePasswordRequest;
import com.konstantin.habittracker.exception.InvalidCredentialsException;
import com.konstantin.habittracker.model.User;
import com.konstantin.habittracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceChangePasswordTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @InjectMocks
    private AuthService authService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setPassword("hashed-current-password");

        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user);
    }

    @Test
    void shouldChangePasswordWhenCurrentPasswordIsCorrect() {
        ChangePasswordRequest request = new ChangePasswordRequest("current-password", "new-password");

        when(passwordEncoder.matches("current-password", "hashed-current-password")).thenReturn(true);
        when(passwordEncoder.encode("new-password")).thenReturn("hashed-new-password");

        authService.changePassword(request);

        assertEquals("hashed-new-password", user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void shouldThrowWhenCurrentPasswordIsIncorrect() {
        ChangePasswordRequest request = new ChangePasswordRequest("wrong-password", "new-password");

        when(passwordEncoder.matches("wrong-password", "hashed-current-password")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.changePassword(request));

        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldNotSaveWhenPasswordCheckFails() {
        ChangePasswordRequest request = new ChangePasswordRequest("wrong-password", "new-password");

        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.changePassword(request));

        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }
}