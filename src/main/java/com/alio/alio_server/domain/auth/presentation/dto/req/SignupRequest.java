package com.alio.alio_server.domain.auth.presentation.dto.req;

import jakarta.validation.constraints.NotBlank;

public record SignupRequest(
        @NotBlank String username,
        @NotBlank String password,
        String nickname
) {
}

