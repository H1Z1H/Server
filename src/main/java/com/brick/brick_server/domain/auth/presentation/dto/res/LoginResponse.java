package com.brick.brick_server.domain.auth.presentation.dto.res;


import com.brick.brick_server.common.jwt.dto.TokenResponse;

public record LoginResponse(
        TokenResponse tokenResponse,
        boolean detail
) {
}
