package com.alio.alio_server.domain.auth.presentation.dto.req;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank String username,
        @NotBlank String password
) {
}

