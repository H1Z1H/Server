package com.brick.brick_server.domain.admin.presentation.dto.req;

public record LoginRequest(
        String id,
        String password
) {
}
