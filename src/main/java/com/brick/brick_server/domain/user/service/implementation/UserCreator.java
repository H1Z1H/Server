package com.brick.brick_server.domain.user.service.implementation;


import com.brick.brick_server.domain.admin.domain.UserCoin;
import com.brick.brick_server.domain.admin.domain.repository.UserCoinRepository;
import com.brick.brick_server.domain.user.domain.UserCloth;
import com.brick.brick_server.domain.user.domain.Users;
import com.brick.brick_server.domain.user.domain.repository.UserClothRepository;
import com.brick.brick_server.domain.user.domain.repository.UserRepository;
import com.brick.brick_server.domain.user.domain.vo.Role;
import com.brick.brick_server.domain.user.exception.InsufficientFundsException;
import com.brick.brick_server.domain.user.exception.InvalidCoinTypeException;
import com.brick.brick_server.domain.user.exception.InvalidPriceFormatException;
import com.brick.brick_server.domain.user.presentation.dto.req.UserClothRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.brick.brick_server.domain.user.exception.UserNotFoundException;


import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserCreator {
    private final UserRepository userRepository;
    private final UserCoinRepository userCoinRepository;
    private final UserClothRepository userClothRepository;

    public Users signup(String userId) {
        Users user = Users.builder()
                .socialAccountUid(userId)
                .role(Role.USER)
                .money(1000000L)
                .build();

        return userRepository.save(user);
    }


    public void createUserCoin(String coinName, String price, Long amount, String type, Long id) {

        Users user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        String date = LocalDate.now().toString();

        Optional<UserCoin> latestCoin = userCoinRepository.findTopByUserIdAndCoinNameOrderByIdDesc(id, coinName);

        Long nowAmount = 0L;

        if (latestCoin.isPresent()) {
            nowAmount = latestCoin.get().getNowAmount();
        }

        // String price → long 변환
        long parsedPrice;
        try {
            parsedPrice = Long.parseLong(price);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid price format: " + price);
        }

        long userMoney = user.getMoney() != null ? user.getMoney() : 0L;

        if (type.equalsIgnoreCase("buy")) {
            if (userMoney < parsedPrice) {
                throw new IllegalArgumentException("보유 자산보다 큰 금액으로 구매할 수 없습니다.");
            }
            nowAmount += amount;
            user.setMoney(userMoney + parsedPrice);
        } else if (type.equalsIgnoreCase("sell")) {
            if (nowAmount < amount) {
                throw new IllegalArgumentException("보유한 코인 수량보다 많이 팔 수 없습니다.");
            }
            nowAmount -= amount;
            user.setMoney(userMoney + parsedPrice);
        } else {
            throw new InvalidPriceFormatException(price);
        }

        UserCoin userCoin = UserCoin.builder()
                .user(user)
                .coinName(coinName)
                .price(price)
                .amount(amount)
                .type(type)
                .date(date)
                .nowAmount(nowAmount)
                .build();

        userCoinRepository.save(userCoin);
        userRepository.save(user);
    }

    public void createUserCloth(UserClothRequest request, Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Long deductAmount;
        String type = request.type();

        if ("clothes".equalsIgnoreCase(type)) {
            deductAmount = 100000L;
        } else if ("accessories".equalsIgnoreCase(type)) {
            deductAmount = 80000L;
        } else {
            throw new IllegalArgumentException("Invalid cloth type: " + type);
        }

        Long newMoney = user.getMoney() - deductAmount;

        if (newMoney < 0) {
            throw new InsufficientFundsException("보유 금액이 부족합니다.");
        }

        // 금액 차감
        user.setMoney(newMoney);

        UserCloth userCloth = UserCloth.builder()
                .user(user)
                .type(type)
                .name(request.name())
                .wear(false)
                .build();

        userClothRepository.save(userCloth);
        userRepository.save(user);
    }
}