package com.konstantin.habittracker.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole userRole;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;

    @Column(name = "verification_code")
    private String verificationCode;

    @Column(name = "verification_code_expiry")
    private Instant verificationCodeExpiry;

    @Column(name = "created_at", nullable = false,  updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public User() {}

    public User(String name, String email, String password, UserRole userRole) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.userRole = userRole;
    }

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    //getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() {
        return email;
    }
    public UserRole getRole() { return userRole; }
    public String getPassword() { return password; }
    public boolean isEmailVerified() { return emailVerified; }
    public String getVerificationCode() { return verificationCode; }
    public Instant getVerificationCodeExpiry() { return verificationCodeExpiry; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    //setters
    public void setId(long l) { this.id = l; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setRole(UserRole userRole) { this.userRole = userRole; }
    public void setPassword(String hashedPassword) { this.password = hashedPassword; }
    public void setEmailVerified(boolean b) { this.emailVerified = b; }
    public void setVerificationCode(String verificationCode) { this.verificationCode = verificationCode; }
    public void setVerificationCodeExpiry(Instant instant) { this.verificationCodeExpiry = instant; }
}
