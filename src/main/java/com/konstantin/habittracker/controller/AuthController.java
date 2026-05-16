package com.konstantin.habittracker.controller;


import com.konstantin.habittracker.business.logic.service.AuthService;
import com.konstantin.habittracker.business.logic.service.AuthenticatedUserService;
import com.konstantin.habittracker.business.logic.service.JwtService;
import com.konstantin.habittracker.business.logic.service.RefreshTokenService;
import com.konstantin.habittracker.dto.request.LoginRequest;
import com.konstantin.habittracker.dto.request.RefreshRequest;
import com.konstantin.habittracker.dto.request.RegisterRequest;
import com.konstantin.habittracker.dto.response.AuthResponse;
import com.konstantin.habittracker.dto.response.RefreshResponse;
import com.konstantin.habittracker.dto.response.UserResponse;
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
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        User user = authenticatedUserService.getAuthenticatedUser();

        UserResponse response = new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        RefreshToken refreshToken = refreshTokenService.findByToken(request.getRefreshToken());
        refreshTokenService.verifyExpiration(refreshToken);
        String newAccessToken = jwtService.generateToken(refreshToken.getUser());
        return ResponseEntity.ok(new RefreshResponse(newAccessToken, 86400));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshRequest request) {
        RefreshToken refreshToken = refreshTokenService.findByToken(request.getRefreshToken());
        refreshTokenService.deleteByUser(refreshToken.getUser());
        return ResponseEntity.noContent().build();
    }
}
