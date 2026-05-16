package com.konstantin.habittracker.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class VerifyEmailRequest
{
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Code is required")
    @Size(min = 6, max = 6) // maybe change the error here?
    private String code;

    public VerifyEmailRequest(String email, String code) {
        this.email = email;
        this.code = code;
    }

    //getters
    public String getEmail() {
        return email;
    }
    public String getCode() {
        return code;
    }

    //setters
    public void setEmail(String email) {
        this.email = email;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
