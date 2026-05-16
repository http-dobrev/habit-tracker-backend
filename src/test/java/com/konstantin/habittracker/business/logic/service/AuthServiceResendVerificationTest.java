package com.konstantin.habittracker.business.logic.service;

import com.konstantin.habittracker.dto.request.ResendVerificationRequest;
import com.konstantin.habittracker.dto.response.ResendVerificationResponse;
import com.konstantin.habittracker.exception.InvalidCredentialsException;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceResendVerificationTest {

    @Mock JwtService jwtService;
    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock RefreshTokenService refreshTokenService;
    @Mock VerificationCodeService verificationCodeService;

    @InjectMocks AuthService authService;

    private User unverifiedUser;
    private User verifiedUser;

    @BeforeEach
    void setUp() {
        unverifiedUser = new User("John", "john@example.com", "hashed", UserRole.USER);
        unverifiedUser.setEmailVerified(false);

        verifiedUser = new User("John", "john@example.com", "hashed", UserRole.USER);
        verifiedUser.setEmailVerified(true);
    }

    @Test
    void resendVerification_success() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(unverifiedUser));

        ResendVerificationResponse response = authService.resendVerification(
                new ResendVerificationRequest("john@example.com"));

        assertThat(response.message()).contains("resent");
        verify(verificationCodeService).sendCode(unverifiedUser);
    }

    @Test
    void resendVerification_throwsWhenUserNotFound() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.resendVerification(
                new ResendVerificationRequest("john@example.com")))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void resendVerification_throwsWhenAlreadyVerified() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(verifiedUser));

        assertThatThrownBy(() -> authService.resendVerification(
                new ResendVerificationRequest("john@example.com")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already verified");
    }
}