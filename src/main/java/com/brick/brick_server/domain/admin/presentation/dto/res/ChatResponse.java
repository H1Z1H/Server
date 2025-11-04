package com.brick.brick_server.domain.admin.presentation.dto.res;

import java.util.List;

public record ChatResponse(
        List<Choice> choices
) {
    public record Choice(Message message) {
        public record Message(String role, String content) {}
    }
}