package com.konstantin.habittracker.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ResendVerificationRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    public ResendVerificationRequest(String email) {
        this.email = email;
    }

    //getters
    public String getEmail() {
        return email;
    }

    //setters
    public void setEmail(String email) {
        this.email = email;
    }
}
