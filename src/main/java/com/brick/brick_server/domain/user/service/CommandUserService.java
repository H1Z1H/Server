package com.brick.brick_server.domain.user.service;

import com.brick.brick_server.domain.user.domain.Users;
import com.brick.brick_server.domain.user.presentation.dto.req.CoinTransactionRequest;
import com.brick.brick_server.domain.user.presentation.dto.req.UserClothRequest;
import com.brick.brick_server.domain.user.presentation.dto.req.WearClothRequest;
import com.brick.brick_server.domain.user.presentation.dto.res.BasicInfoResponse;
import com.brick.brick_server.domain.user.service.implementation.UserCreator;
import com.brick.brick_server.domain.user.service.implementation.UserUpdater;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommandUserService {
    private final UserCreator userCreator;
    private final UserUpdater userUpdater;

    public void buyOrSellCoin(CoinTransactionRequest req,Long id) {
        userCreator.createUserCoin(req.coinName(),req.price(),req.amount(),req.type(),id);
    }

    public void createUserCloth(UserClothRequest request, Long userId) {
        userCreator.createUserCloth(request, userId);
    }

    public void wearCloth(WearClothRequest request, Long userId) {
        userUpdater.wearCloth(request, userId);
    }

    public void addMoney(Long userId) {
        userUpdater.addMoney(userId);
    }
}

