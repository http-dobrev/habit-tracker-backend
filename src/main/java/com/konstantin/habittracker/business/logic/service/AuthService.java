package com.konstantin.habittracker.business.logic.service;

import com.konstantin.habittracker.dto.request.*;
import com.konstantin.habittracker.dto.response.AuthResponse;
import com.konstantin.habittracker.dto.response.RegisterResponse;
import com.konstantin.habittracker.dto.response.ResendVerificationResponse;
import com.konstantin.habittracker.dto.response.UserResponse;
import com.konstantin.habittracker.exception.EmailAlreadyExistsException;
import com.konstantin.habittracker.exception.InvalidCredentialsException;
import com.konstantin.habittracker.model.RefreshToken;
import com.konstantin.habittracker.model.UserRole;
import com.konstantin.habittracker.model.User;
import com.konstantin.habittracker.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final VerificationCodeService verificationCodeService;
    private final AuthenticatedUserService authenticatedUserService;

    public AuthService(JwtService jwtService,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       RefreshTokenService refreshTokenService,
                       VerificationCodeService verificationCodeService,
                       AuthenticatedUserService authenticatedUserService) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
        this.verificationCodeService = verificationCodeService;
        this.authenticatedUserService = authenticatedUserService;
    }

    public RegisterResponse register(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        User user = new User(
                request.getName(),
                email,
                passwordEncoder.encode(request.getPassword()),
                UserRole.USER
        );
        user.setEmailVerified(false);

        userRepository.save(user);
        verificationCodeService.sendCode(user);

        return new RegisterResponse("Verification email sent. Please check your inbox.");
    }

    public AuthResponse verifyEmail(VerifyEmailRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or code"));

        verificationCodeService.verifyCode(user, request.getCode());

        user.setEmailVerified(true);
        user.setVerificationCode(null);
        user.setVerificationCodeExpiry(null);
        userRepository.save(user);

        return buildAuthResponse(user);
    }

    public ResendVerificationResponse resendVerification(ResendVerificationRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("No account found with that email"));

        if (user.isEmailVerified()) {
            throw new IllegalStateException("Email is already verified");
        }

        verificationCodeService.sendCode(user);

        return new ResendVerificationResponse("Verification code resent.");
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        if (!user.isEmailVerified()) {
            throw new InvalidCredentialsException("Please verify your email before logging in");
        }

        return buildAuthResponse(user);
    }

    public void changePassword(ChangePasswordRequest request) {
        User user = authenticatedUserService.getAuthenticatedUser();

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid current password");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        String accessToken = jwtService.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        UserResponse userResponse = new UserResponse(
                user.getId(), user.getName(), user.getEmail(), user.getRole().name()
        );
        return new AuthResponse(accessToken, refreshToken.getToken(), jwtService.getExpirationInSeconds(), userResponse);
    }

    public void deleteAccount(DeleteAccountRequest request) {
        RefreshToken refreshToken = refreshTokenService.findByToken(request.refreshToken());
        refreshTokenService.verifyExpiration(refreshToken);
        User user = refreshToken.getUser();
        refreshTokenService.deleteByUser(user);
        userRepository.delete(user);
    }
}