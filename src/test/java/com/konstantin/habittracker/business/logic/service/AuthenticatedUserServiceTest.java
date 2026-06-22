package com.konstantin.habittracker.business.logic.service;

import com.konstantin.habittracker.exception.UserNotFoundException;
import com.konstantin.habittracker.model.User;
import com.konstantin.habittracker.model.UserRole;
import com.konstantin.habittracker.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticatedUserServiceTest {

    @Mock UserRepository userRepository;

    @InjectMocks AuthenticatedUserService authenticatedUserService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void setAuthenticatedEmail(String email) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(email, null);
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void getAuthenticatedUser_returnsUserMatchingSecurityContextEmail() {
        setAuthenticatedEmail("john@example.com");
        User user = new User("John", "john@example.com", "hashed", UserRole.USER);
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        User result = authenticatedUserService.getAuthenticatedUser();

        assertThat(result).isEqualTo(user);
    }

    @Test
    void getAuthenticatedUser_throwsWhenUserNotFound() {
        setAuthenticatedEmail("ghost@example.com");
        when(userRepository.findByEmail("ghost@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authenticatedUserService.getAuthenticatedUser())
                .isInstanceOf(UserNotFoundException.class);
    }
}