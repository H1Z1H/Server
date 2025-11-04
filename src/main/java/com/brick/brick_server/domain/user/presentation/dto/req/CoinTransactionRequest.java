package com.brick.brick_server.domain.user.presentation.dto.req;

public record CoinTransactionRequest(
        String coinName,
        String price,
        Long amount,
        String type // "buy" 또는 "sell"
) {}