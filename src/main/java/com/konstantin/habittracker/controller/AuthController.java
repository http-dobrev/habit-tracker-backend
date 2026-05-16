package com.konstantin.habittracker.controller;


import com.konstantin.habittracker.business.logic.service.AuthService;
import com.konstantin.habittracker.business.logic.service.AuthenticatedUserService;
import com.konstantin.habittracker.business.logic.service.JwtService;
import com.konstantin.habittracker.business.logic.service.RefreshTokenService;
import com.konstantin.habittracker.dto.request.*;
import com.konstantin.habittracker.dto.response.*;
import com.konstantin.habittracker.model.RefreshToken;
import com.konstantin.habittracker.model.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthenticatedUserService authenticatedUserService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    public AuthController(AuthService authService,
                          AuthenticatedUserService authenticatedUserService,
                          RefreshTokenService refreshTokenService,
                          JwtService jwtService) {
        this.authService = authService;
        this.authenticatedUserService = authenticatedUserService;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/verify-email")
    public ResponseEntity<AuthResponse> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        return ResponseEntity.ok(authService.verifyEmail(request));
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<ResendVerificationResponse> resendVerification(@Valid @RequestBody ResendVerificationRequest request) {
        return ResponseEntity.ok(authService.resendVerification(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        User user = authenticatedUserService.getAuthenticatedUser();
        return ResponseEntity.ok(new UserResponse(
                user.getId(), user.getName(), user.getEmail(), user.getRole().name()
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        RefreshToken refreshToken = refreshTokenService.findByToken(request.getRefreshToken());
        refreshTokenService.verifyExpiration(refreshToken);
        String newAccessToken = jwtService.generateToken(refreshToken.getUser());
        return ResponseEntity.ok(new RefreshResponse(newAccessToken, jwtService.getExpirationInSeconds()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshRequest request) {
        RefreshToken refreshToken = refreshTokenService.findByToken(request.getRefreshToken());
        refreshTokenService.deleteByUser(refreshToken.getUser());
        return ResponseEntity.noContent().build();
    }
}
