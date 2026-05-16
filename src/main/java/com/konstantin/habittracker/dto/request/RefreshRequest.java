package com.konstantin.habittracker.dto.request;

import jakarta.validation.constraints.NotBlank;

public class RefreshRequest {
    @NotBlank
    private String refreshToken;
    public String getRefreshToken() { return refreshToken; }
}