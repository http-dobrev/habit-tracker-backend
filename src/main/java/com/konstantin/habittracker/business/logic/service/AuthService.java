package com.konstantin.habittracker.business.logic.service;

import com.konstantin.habittracker.dto.request.LoginRequest;
import com.konstantin.habittracker.dto.request.RegisterRequest;
import com.konstantin.habittracker.dto.response.AuthResponse;
import com.konstantin.habittracker.dto.response.UserResponse;
import com.konstantin.habittracker.exception.EmailAlreadyExistsException;
import com.konstantin.habittracker.exception.InvalidCredentialsException;
import com.konstantin.habittracker.model.RefreshToken;
import com.konstantin.habittracker.model.UserRole;
import com.konstantin.habittracker.model.User;
import com.konstantin.habittracker.repository.UserRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    public AuthService(JwtService jwtService, UserRepository userRepository, PasswordEncoder passwordEncoder, RefreshTokenService refreshTokenService) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
    }

    public AuthResponse register(RegisterRequest request) {

        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User(
                request.getName(),
                email,
                hashedPassword,
                UserRole.USER
        );

        User savedUser = userRepository.save(user);

        return getAuthResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(email).orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        boolean passwordMatches = passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        );

        if (!passwordMatches) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        return getAuthResponse(user);
    }

    @NonNull
    private AuthResponse getAuthResponse(User user) {
        String accessToken = jwtService.generateToken(user);
        int expirationInSeconds = jwtService.getExpirationInSeconds();
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name()
        );

        return new AuthResponse(accessToken, refreshToken.getToken(), expirationInSeconds, userResponse);
    }
}