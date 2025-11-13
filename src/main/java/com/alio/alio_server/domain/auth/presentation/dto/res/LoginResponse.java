package com.alio.alio_server.domain.auth.presentation.dto.res;


import com.alio.alio_server.common.jwt.dto.TokenResponse;

public record LoginResponse(
        TokenResponse tokenResponse,
        boolean detail
) {
}
