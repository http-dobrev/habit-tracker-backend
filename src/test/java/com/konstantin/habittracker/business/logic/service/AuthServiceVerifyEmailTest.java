package com.konstantin.habittracker.business.logic.service;

import com.konstantin.habittracker.dto.request.VerifyEmailRequest;
import com.konstantin.habittracker.dto.response.AuthResponse;
import com.konstantin.habittracker.exception.InvalidCredentialsException;
import com.konstantin.habittracker.model.RefreshToken;
import com.konstantin.habittracker.model.User;
import com.konstantin.habittracker.model.UserRole;
import com.konstantin.habittracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceVerifyEmailTest {

    @Mock JwtService jwtService;
    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock RefreshTokenService refreshTokenService;
    @Mock VerificationCodeService verificationCodeService;

    @InjectMocks AuthService authService;

    private User unverifiedUser;
    private RefreshToken refreshToken;

    @BeforeEach
    void setUp() {
        unverifiedUser = new User("John", "john@example.com", "hashed", UserRole.USER);
        unverifiedUser.setEmailVerified(false);

        refreshToken = new RefreshToken();
        refreshToken.setToken("refresh-token-value");
        refreshToken.setUser(unverifiedUser);
        refreshToken.setExpiryDate(Instant.now().plusSeconds(86400));
    }

    @Test
    void verifyEmail_success() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(unverifiedUser));
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(jwtService.generateToken(any())).thenReturn("access-token");
        when(jwtService.getExpirationInSeconds()).thenReturn(86400);
        when(refreshTokenService.createRefreshToken(any())).thenReturn(refreshToken);

        AuthResponse response = authService.verifyEmail(
                new VerifyEmailRequest("john@example.com", "123456"));

        assertThat(response.token()).isEqualTo("access-token");
        assertThat(response.refreshToken()).isEqualTo("refresh-token-value");
        assertThat(unverifiedUser.isEmailVerified()).isTrue();
        assertThat(unverifiedUser.getVerificationCode()).isNull();
        assertThat(unverifiedUser.getVerificationCodeExpiry()).isNull();
    }

    @Test
    void verifyEmail_throwsWhenUserNotFound() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.verifyEmail(
                new VerifyEmailRequest("john@example.com", "123456")))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void verifyEmail_throwsWhenCodeInvalid() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(unverifiedUser));
        doThrow(new InvalidCredentialsException("Invalid verification code."))
                .when(verificationCodeService).verifyCode(any(), eq("wrongcode"));

        assertThatThrownBy(() -> authService.verifyEmail(
                new VerifyEmailRequest("john@example.com", "wrongcode")))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Invalid verification code");
    }
}