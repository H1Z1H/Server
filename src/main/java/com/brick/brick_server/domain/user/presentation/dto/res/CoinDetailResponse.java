package com.brick.brick_server.domain.user.presentation.dto.res;

public record CoinDetailResponse(
        String name,
        String price,
        String date
) {
    public static CoinDetailResponse from(com.brick.brick_server.domain.admin.domain.Coin coin) {
        return new CoinDetailResponse(
                coin.getName(),
                coin.getPrice(),
                coin.getDate()
        );
    }
}