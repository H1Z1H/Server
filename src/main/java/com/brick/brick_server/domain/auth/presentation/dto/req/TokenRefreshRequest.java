package com.brick.brick_server.domain.auth.presentation.dto.req;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record TokenRefreshRequest(
        @Schema(description = "리프레시 토큰")
        @NotBlank String refreshToken
) {
}

