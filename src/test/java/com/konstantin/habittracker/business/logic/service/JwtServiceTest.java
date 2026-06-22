package com.konstantin.habittracker.business.logic.service;

import com.konstantin.habittracker.model.User;
import com.konstantin.habittracker.model.UserRole;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    // 33 chars/bytes -> satisfies the minimum 32-byte key length JJWT requires for HS256
    private static final String TEST_SECRET = "01234567890123456789012345678901";

    private JwtService jwtService;
    private User user;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", TEST_SECRET);

        user = new User("John", "john@example.com", "hashed", UserRole.USER);
        user.setId(1L);
    }

    @Test
    void generateToken_containsExpectedClaims() {
        String token = jwtService.generateToken(user);

        var claims = Jwts.parser()
                .verifyWith(jwtService.getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertThat(claims.getSubject()).isEqualTo("john@example.com");
        assertThat(claims.get("userId").toString()).isEqualTo("1");
        assertThat(claims.get("email", String.class)).isEqualTo("john@example.com");
        assertThat(claims.get("role", String.class)).isEqualTo("USER");

        long diffSeconds = (claims.getExpiration().getTime() - claims.getIssuedAt().getTime()) / 1000;
        assertThat(diffSeconds).isEqualTo(86400);
    }

    @Test
    void getExpirationInSeconds_returnsOneDayInSeconds() {
        assertThat(jwtService.getExpirationInSeconds()).isEqualTo(86400);
    }

    @Test
    void getSigningKey_isConsistentAcrossCalls() {
        SecretKey key1 = jwtService.getSigningKey();
        SecretKey key2 = jwtService.getSigningKey();

        assertThat(key1.getEncoded()).isEqualTo(key2.getEncoded());
    }

    @Test
    void extractEmail_returnsEmailFromValidToken() {
        String token = jwtService.generateToken(user);

        assertThat(jwtService.extractEmail(token)).isEqualTo("john@example.com");
    }

    @Test
    void isTokenValid_returnsTrueForValidToken() {
        String token = jwtService.generateToken(user);

        assertThat(jwtService.isTokenValid(token)).isTrue();
    }

    @Test
    void isTokenValid_returnsFalseForMalformedToken() {
        assertThat(jwtService.isTokenValid("not-a-real-token")).isFalse();
    }

    @Test
    void isTokenValid_returnsFalseForTokenSignedWithDifferentKey() {
        SecretKey wrongKey = Keys.hmacShaKeyFor(
                "99999999999999999999999999999999".getBytes(StandardCharsets.UTF_8));

        String tokenWithWrongSignature = Jwts.builder()
                .subject("john@example.com")
                .signWith(wrongKey)
                .compact();

        assertThat(jwtService.isTokenValid(tokenWithWrongSignature)).isFalse();
    }

    @Test
    void isTokenValid_returnsFalseForExpiredToken() {
        Date past = new Date(System.currentTimeMillis() - 10_000);

        String expiredToken = Jwts.builder()
                .subject("john@example.com")
                .issuedAt(new Date(past.getTime() - 1000))
                .expiration(past)
                .signWith(jwtService.getSigningKey())
                .compact();

        assertThat(jwtService.isTokenValid(expiredToken)).isFalse();
    }
}