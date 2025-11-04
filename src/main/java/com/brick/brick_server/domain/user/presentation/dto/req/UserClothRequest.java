package com.brick.brick_server.domain.user.presentation.dto.req;

public record UserClothRequest(
        String type,  // clothes 또는 accessories
        String name
) {}