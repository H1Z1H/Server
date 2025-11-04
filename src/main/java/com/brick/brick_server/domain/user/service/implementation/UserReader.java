package com.brick.brick_server.domain.user.service.implementation;


import com.brick.brick_server.domain.admin.domain.repository.CoinRepository;
import com.brick.brick_server.domain.user.domain.Users;
import com.brick.brick_server.domain.user.domain.repository.UserRepository;
import com.brick.brick_server.domain.user.presentation.dto.res.CoinDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.brick.brick_server.domain.user.exception.UserNotFoundException;


import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserReader {
    private final UserRepository userRepository;
    private final CoinRepository coinRepository;

    public Optional<Users> findBySocialAccountUid(String id) {
        return userRepository.findBySocialAccountUid(id);
    }

    public Users findById(Long id){
        return userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
    }

    public List<CoinDetailResponse> getCoinDetail(String name) {
        return coinRepository.findByNameOrderByDateAsc(name).stream()
                .map(CoinDetailResponse::from)
                .toList();
    }

    public List<Users> getUsersSortedByMoney() {
        return userRepository.findAllByOrderByMoneyDesc();
    }
}
