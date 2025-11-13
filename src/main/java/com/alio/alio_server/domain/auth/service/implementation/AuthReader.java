package com.alio.alio_server.domain.auth.service.implementation;



import com.alio.alio_server.domain.auth.domain.Token;
import com.alio.alio_server.domain.auth.domain.repository.TokenRepository;
import com.alio.alio_server.domain.auth.exception.TokenNotFoundException;
import com.alio.alio_server.domain.user.domain.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthReader {
    private final TokenRepository tokenRepository;

    public Optional<Token> findByUser(Users user) {
        return tokenRepository.findByUser(user);
    }

    public Token findByRefreshToken(String refreshToken) {
        return tokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(TokenNotFoundException::new);
    }
}