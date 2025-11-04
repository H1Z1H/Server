package com.brick.brick_server.domain.user.service.implementation;

import com.brick.brick_server.domain.user.domain.DailyMoneyLog;
import com.brick.brick_server.domain.user.domain.UserCloth;
import com.brick.brick_server.domain.user.domain.Users;
import com.brick.brick_server.domain.user.domain.repository.DailyMoneyLogRepository;
import com.brick.brick_server.domain.user.domain.repository.UserClothRepository;
import com.brick.brick_server.domain.user.domain.repository.UserRepository;
import com.brick.brick_server.domain.user.exception.ClothNotFoundException;
import com.brick.brick_server.domain.user.exception.DailyMoneyAlreadyReceivedException;
import com.brick.brick_server.domain.user.exception.UserNotFoundException;
import com.brick.brick_server.domain.user.presentation.dto.req.WearClothRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserUpdater {

    private final UserRepository userRepository;
    private final UserClothRepository userClothRepository;
    private final DailyMoneyLogRepository moneyLogRepository;

    public void updateDetail(Users user, String nickname,String gender) {
        user.updateDetail(nickname,gender);
        userRepository.save(user);
    }

    public void wearCloth(WearClothRequest request, Long userId) {
        applyWear(userId, "clothes", request.clothes());
        applyWear(userId, "accessories", request.accessories());
    }

    private void applyWear(Long userId, String type, String name) {
        // 기존 착용 중인 해당 타입의 아이템은 모두 벗김
        List<UserCloth> wornClothes = userClothRepository
                .findByUserIdAndTypeAndWearTrue(userId, type);
        for (UserCloth cloth : wornClothes) {
            cloth.takeOff();
        }

        // name이 비어있으면 착용은 하지 않고 return
        if (name == null || name.isBlank()) return;

        // 착용할 아이템 조회 및 착용
        UserCloth toWear = userClothRepository
                .findByUserIdAndTypeAndName(userId, type, name)
                .orElseThrow(ClothNotFoundException::new);

        toWear.wear();
    }


    public void addMoney(Long userId) {
        LocalDate today = LocalDate.now();

        boolean alreadyReceived = moneyLogRepository.findByUserIdAndDate(userId, today).isPresent();
        if (alreadyReceived) {
            throw new DailyMoneyAlreadyReceivedException();
        }

        Users user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Long currentMoney = user.getMoney() == null ? 0L : user.getMoney();
        user.setMoney(currentMoney + 100_000L);

        moneyLogRepository.save(DailyMoneyLog.builder()
                .userId(userId)
                .date(today)
                .build());
    }
}
