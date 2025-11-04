package com.brick.brick_server.domain.admin.presentation.dto.req;

import java.util.List;

public record ChatRequest(
        String model,
        List<Message> messages
) {
    public record Message(String role, String content) {}
}