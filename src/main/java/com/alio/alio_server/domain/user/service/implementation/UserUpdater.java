package com.alio.alio_server.domain.user.service.implementation;

import com.alio.alio_server.domain.user.domain.Users;
import com.alio.alio_server.domain.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserUpdater {

    private final UserRepository userRepository;

    public void updateNickname(Users user, String nickname) {
        String normalized = nickname == null ? null : nickname.trim();
        if (normalized != null && normalized.isEmpty()) {
            normalized = null;
        }
        user.updateNickname(normalized);
        userRepository.save(user);
    }
}
