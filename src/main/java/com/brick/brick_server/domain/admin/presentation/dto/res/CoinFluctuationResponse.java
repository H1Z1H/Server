package com.brick.brick_server.domain.admin.presentation.dto.res;

public record CoinFluctuationResponse(
        String coinName,             // 코인 이름
        double fluctuationPercent,   // 변동률
        String previousPrice,        // 이전 가격
        String currentPrice,         // 현재 가격
        Long totalUserHolding        // 전체 유저가 보유 중인 수량 (nowAmount 기준 합계)
) {}