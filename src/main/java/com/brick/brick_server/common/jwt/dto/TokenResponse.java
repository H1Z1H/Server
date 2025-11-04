package com.brick.brick_server.common.jwt.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}