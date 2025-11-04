package com.brick.brick_server.domain.auth.service.implementation;

import com.brick.brick_server.domain.auth.domain.Token;
import org.springframework.stereotype.Service;

@Service
public class AuthDeleter {
    public void deleteRefreshToken(Token token) {
        token.deleteRefreshToken();
    }
}