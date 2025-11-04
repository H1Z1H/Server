package com.brick.brick_server.domain.user.presentation.dto.res;

import com.brick.brick_server.domain.user.domain.UserCloth;

public record UserClothResponse(
        Long id,
        String type,
        String name,
        boolean wear
) {
    public static UserClothResponse from(UserCloth cloth) {
        return new UserClothResponse(
                cloth.getId(),
                cloth.getType(),
                cloth.getName(),
                cloth.isWear()
        );
    }
}