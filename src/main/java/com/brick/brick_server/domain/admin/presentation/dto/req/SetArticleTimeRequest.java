package com.brick.brick_server.domain.admin.presentation.dto.req;

public record SetArticleTimeRequest(
        Long id,
        String date,
        String time
) {}