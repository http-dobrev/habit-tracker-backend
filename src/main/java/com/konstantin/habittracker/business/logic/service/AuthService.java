package com.konstantin.habittracker.business.logic.service;

import com.konstantin.habittracker.dto.request.LoginRequest;
import com.konstantin.habittracker.dto.request.RegisterRequest;
import com.konstantin.habittracker.dto.response.AuthResponse;
import com.konstantin.habittracker.exception.EmailAlreadyExistsException;
import com.konstantin.habittracker.exception.InvalidCredentialsException;
import com.konstantin.habittracker.exception.InvalidRequestException;
import com.konstantin.habittracker.model.Role;
import com.konstantin.habittracker.model.User;
import com.konstantin.habittracker.persistence.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class AuthService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(JwtService jwtService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
                Role.USER
        );

        User savedUser = userRepository.save(user);

        String token = jwtService.generateToken(savedUser);
        Integer expirationInSeconds = jwtService.getExpirationInSeconds();

        return new AuthResponse(
                token,
                expirationInSeconds,
                "User successfully registered"
        );
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

        String token = jwtService.generateToken(user);

        return new AuthResponse(
                token,
                jwtService.getExpirationInSeconds(),
                "User successfully logged in"
        );
    }
}