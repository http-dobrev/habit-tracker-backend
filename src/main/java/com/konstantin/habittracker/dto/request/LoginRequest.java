package com.konstantin.habittracker.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    //getters
    public String getEmail() { return email; }
    public String getPassword() { return password; }

    //setters
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
}
