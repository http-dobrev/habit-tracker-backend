package com.konstantin.habittracker.business.logic.service;

import com.konstantin.habittracker.model.RefreshToken;
import com.konstantin.habittracker.model.User;
import com.konstantin.habittracker.model.UserRole;
import com.konstantin.habittracker.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    private static final long REFRESH_EXPIRATION_MS = 2_592_000_000L; // 30 days

    @Mock RefreshTokenRepository refreshTokenRepository;

    @InjectMocks RefreshTokenService refreshTokenService;

    private User user;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(refreshTokenService, "refreshExpirationMs", REFRESH_EXPIRATION_MS);
        user = new User("John", "john@example.com", "hashed", UserRole.USER);
        user.setId(1L);
    }

    @Test
    void createRefreshToken_deletesExistingTokenForUserFirst() {
        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        refreshTokenService.createRefreshToken(user);

        verify(refreshTokenRepository).deleteByUser(user);
    }

    @Test
    void createRefreshToken_generatesTokenAndExpiryThirtyDaysOut() {
        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Instant before = Instant.now();
        RefreshToken result = refreshTokenService.createRefreshToken(user);

        assertThat(result.getToken()).isNotBlank();
        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.getExpiryDate()).isAfter(before.plusMillis(REFRESH_EXPIRATION_MS - 5000));
        assertThat(result.getExpiryDate()).isBefore(before.plusMillis(REFRESH_EXPIRATION_MS + 5000));
    }

    @Test
    void createRefreshToken_savesTheGeneratedToken() {
        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        refreshTokenService.createRefreshToken(user);

        verify(refreshTokenRepository).save(captor.capture());
        assertThat(captor.getValue().getUser()).isEqualTo(user);
    }

    @Test
    void verifyExpiration_returnsTokenWhenNotExpired() {
        RefreshToken token = new RefreshToken();
        token.setExpiryDate(Instant.now().plusSeconds(3600));

        RefreshToken result = refreshTokenService.verifyExpiration(token);

        assertThat(result).isSameAs(token);
        verify(refreshTokenRepository, never()).delete(any());
    }

    @Test
    void verifyExpiration_deletesAndThrowsWhenExpired() {
        RefreshToken token = new RefreshToken();
        token.setExpiryDate(Instant.now().minusSeconds(60));

        assertThatThrownBy(() -> refreshTokenService.verifyExpiration(token))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("expired");

        verify(refreshTokenRepository).delete(token);
    }

    @Test
    void findByToken_returnsTokenWhenFound() {
        RefreshToken token = new RefreshToken();
        token.setToken("abc-123");
        when(refreshTokenRepository.findByToken("abc-123")).thenReturn(Optional.of(token));

        RefreshToken result = refreshTokenService.findByToken("abc-123");

        assertThat(result).isSameAs(token);
    }

    @Test
    void findByToken_throwsWhenNotFound() {
        when(refreshTokenRepository.findByToken("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> refreshTokenService.findByToken("missing"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void deleteByUser_delegatesToRepository() {
        refreshTokenService.deleteByUser(user);

        verify(refreshTokenRepository).deleteByUser(user);
    }
}