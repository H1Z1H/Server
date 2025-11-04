package com.brick.brick_server.domain.user.presentation.dto.res;

public record UserRankingResponse(
        String nickname,
        String gender,
        Long money,
        String clothes,
        String accessories
) {}