package com.konstantin.habittracker.business.logic.service;

import com.konstantin.habittracker.exception.InvalidCredentialsException;
import com.konstantin.habittracker.model.User;
import com.konstantin.habittracker.repository.UserRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class VerificationCodeService {

    private static final long CODE_EXPIRY_MINUTES = 15;

    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    public VerificationCodeService(UserRepository userRepository, JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.mailSender = mailSender;
    }

    public void sendCode(User user) {
        String code = String.format("%06d", new SecureRandom().nextInt(1_000_000));

        user.setVerificationCode(code);
        user.setVerificationCodeExpiry(Instant.now().plus(CODE_EXPIRY_MINUTES, ChronoUnit.MINUTES));
        userRepository.save(user);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Verify your email");
        message.setText("Your verification code is: " + code + "\n\nExpires in " + CODE_EXPIRY_MINUTES + " minutes.");
        mailSender.send(message);
    }

    public void verifyCode(User user, String code) {
        if (user.getVerificationCode() == null || user.getVerificationCodeExpiry() == null) {
            throw new InvalidCredentialsException("No verification code found. Request a new one.");
        }
        if (Instant.now().isAfter(user.getVerificationCodeExpiry())) {
            throw new InvalidCredentialsException("Verification code has expired. Request a new one.");
        }
        if (!user.getVerificationCode().equals(code)) {
            throw new InvalidCredentialsException("Invalid verification code.");
        }
    }
}