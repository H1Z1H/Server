package com.alio.alio_server.common.jwt.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}