package com.konstantin.habittracker.dto.request;

import jakarta.validation.constraints.NotBlank;

public record DeleteAccountRequest(
        @NotBlank String refreshToken
) {}