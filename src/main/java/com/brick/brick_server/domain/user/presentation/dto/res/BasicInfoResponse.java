package com.brick.brick_server.domain.user.presentation.dto.res;

import com.brick.brick_server.domain.user.domain.Users;

import java.util.List;

public record BasicInfoResponse(
        Long id,
        String nickname,
        String role,
        String gender,
        Long money,
        List<UserClothResponse> clothes,
        List<UserClothResponse> accessories,
        List<UserCoinResponse> coins
) {
    public static BasicInfoResponse from(Users user) {
        return new BasicInfoResponse(
                user.getId(),
                user.getNickname(),
                user.getRole().name(),
                user.getGender(),
                user.getMoney(),
                user.getUserClothes().stream()
                        .filter(c -> c.getType().equals("clothes"))
                        .map(UserClothResponse::from)
                        .toList(),
                user.getUserClothes().stream()
                        .filter(c -> c.getType().equals("accessories"))
                        .map(UserClothResponse::from)
                        .toList(),
                user.getUserCoins().stream()
                        .map(UserCoinResponse::from)
                        .toList()
        );
    }
}