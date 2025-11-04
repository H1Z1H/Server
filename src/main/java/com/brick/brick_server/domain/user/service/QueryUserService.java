package com.brick.brick_server.domain.user.service;


import com.brick.brick_server.domain.user.domain.Users;
import com.brick.brick_server.domain.user.presentation.dto.res.BasicInfoResponse;
import com.brick.brick_server.domain.user.presentation.dto.res.CoinDetailResponse;
import com.brick.brick_server.domain.user.presentation.dto.res.UserRankingResponse;
import com.brick.brick_server.domain.user.service.implementation.UserReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.brick.brick_server.domain.user.domain.UserCloth;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QueryUserService {
    private final UserReader userReader;
    public BasicInfoResponse getBasicInfo(Long id) {
        Users user = userReader.findById(id);
        return BasicInfoResponse.from(user);
    }

    public List<CoinDetailResponse> getCoinDetail(String name) {
        return userReader.getCoinDetail(name);
    }

    public List<UserRankingResponse> getUserRankings() {
        return userReader.getUsersSortedByMoney().stream()
                .map(user -> {
                    // clothes 착용 중인 항목 (있으면 반환, 없으면 null)
                    String clothes = user.getUserClothes().stream()
                            .filter(cloth -> cloth.isWear() && "clothes".equals(cloth.getType()))
                            .map(UserCloth::getName)
                            .findFirst()
                            .orElse(null);

                    // accessories 착용 중인 항목 (있으면 반환, 없으면 null)
                    String accessories = user.getUserClothes().stream()
                            .filter(cloth -> cloth.isWear() && "accessories".equals(cloth.getType()))
                            .map(UserCloth::getName)
                            .findFirst()
                            .orElse(null);

                    return new UserRankingResponse(
                            user.getNickname(),
                            user.getGender(),
                            user.getMoney() != null ? user.getMoney() : 0L,
                            clothes,
                            accessories
                    );
                })
                .toList();
    }
}
