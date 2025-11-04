package com.brick.brick_server.domain.admin.presentation.dto.req;

public record UpdateArticleRequest(
        Long id,
        String content
) {}
