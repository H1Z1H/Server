package com.brick.brick_server.domain.user.presentation.dto.res;

import com.brick.brick_server.domain.admin.domain.UserCoin;

public record UserCoinResponse(
        Long id,
        String coinName,
        String type,
        String price,
        Long amount,
        String date,
        Long nowAmount

) {
    public static UserCoinResponse from(UserCoin coin) {
        return new UserCoinResponse(
                coin.getId(),
                coin.getCoinName(),
                coin.getType(),
                coin.getPrice(),
                coin.getAmount(),
                coin.getDate(),
                coin.getNowAmount()
        );
    }
}