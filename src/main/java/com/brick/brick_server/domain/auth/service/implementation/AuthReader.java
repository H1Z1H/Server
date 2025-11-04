package com.brick.brick_server.domain.auth.service.implementation;



import com.brick.brick_server.domain.auth.domain.Token;
import com.brick.brick_server.domain.auth.domain.repository.TokenRepository;
import com.brick.brick_server.domain.user.domain.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.brick.brick_server.domain.auth.exception.TokenNotFoundException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthReader {
    private final TokenRepository tokenRepository;

    public Token findByUser(Optional<Users> user) {
        return tokenRepository.findByUser(user)
                .orElseThrow(TokenNotFoundException::new);
    }

    public Token findByRefreshToken(String refreshToken) {
        return tokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(TokenNotFoundException::new);
    }
}