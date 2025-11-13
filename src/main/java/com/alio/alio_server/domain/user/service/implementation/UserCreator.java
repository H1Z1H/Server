package com.alio.alio_server.domain.user.service.implementation;

import com.alio.alio_server.domain.user.domain.Users;
import com.alio.alio_server.domain.user.domain.repository.UserRepository;
import com.alio.alio_server.domain.user.domain.vo.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCreator {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Users signup(String username, String rawPassword, String nickname) {
        Users user = Users.builder()
                .username(username)
                .password(passwordEncoder.encode(rawPassword))
                .nickname(normalizeNickname(nickname))
                .role(Role.USER)
                .build();

        return userRepository.save(user);
    }

    private String normalizeNickname(String nickname) {
        if (nickname == null) {
            return null;
        }
        String trimmed = nickname.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}