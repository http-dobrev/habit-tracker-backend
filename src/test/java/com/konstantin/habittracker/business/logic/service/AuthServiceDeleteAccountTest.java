package com.konstantin.habittracker.business.logic.service;

import com.konstantin.habittracker.dto.request.DeleteAccountRequest;
import com.konstantin.habittracker.exception.InvalidCredentialsException;
import com.konstantin.habittracker.model.RefreshToken;
import com.konstantin.habittracker.model.User;
import com.konstantin.habittracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceDeleteAccountTest {

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    private User user;
    private RefreshToken refreshToken;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        refreshToken = new RefreshToken();
        refreshToken.setToken("valid-refresh-token");
        refreshToken.setUser(user);
    }

    @Test
    void shouldDeleteAccountWhenRefreshTokenIsValid() {
        DeleteAccountRequest request = new DeleteAccountRequest("valid-refresh-token");

        when(refreshTokenService.findByToken("valid-refresh-token")).thenReturn(refreshToken);

        authService.deleteAccount(request);

        verify(refreshTokenService).deleteByUser(user);
        verify(userRepository).delete(user);
    }

    @Test
    void shouldThrowWhenRefreshTokenIsExpired() {
        DeleteAccountRequest request = new DeleteAccountRequest("expired-token");

        when(refreshTokenService.findByToken("expired-token")).thenReturn(refreshToken);
        doThrow(new InvalidCredentialsException("Refresh token expired"))
                .when(refreshTokenService).verifyExpiration(refreshToken);

        assertThrows(InvalidCredentialsException.class, () -> authService.deleteAccount(request));

        verify(userRepository, never()).delete(any());
    }

    @Test
    void shouldThrowWhenRefreshTokenNotFound() {
        DeleteAccountRequest request = new DeleteAccountRequest("unknown-token");

        when(refreshTokenService.findByToken("unknown-token"))
                .thenThrow(new InvalidCredentialsException("Refresh token not found"));

        assertThrows(InvalidCredentialsException.class, () -> authService.deleteAccount(request));

        verify(userRepository, never()).delete(any());
    }
}