package com.alio.alio_server.domain.auth.service;

import com.alio.alio_server.common.jwt.dto.TokenResponse;
import com.alio.alio_server.domain.auth.domain.Token;
import com.alio.alio_server.domain.auth.presentation.dto.req.DetailRequest;
import com.alio.alio_server.domain.auth.presentation.dto.req.LoginRequest;
import com.alio.alio_server.domain.auth.presentation.dto.req.SignupRequest;
import com.alio.alio_server.domain.auth.presentation.dto.req.TokenRefreshRequest;
import com.alio.alio_server.domain.auth.presentation.dto.res.LoginResponse;
import com.alio.alio_server.domain.auth.service.implementation.AuthDeleter;
import com.alio.alio_server.domain.auth.service.implementation.AuthReader;
import com.alio.alio_server.domain.auth.service.implementation.AuthUpdater;
import com.alio.alio_server.domain.user.domain.Users;
import com.alio.alio_server.domain.user.service.implementation.UserCreator;
import com.alio.alio_server.domain.user.service.implementation.UserDeleter;
import com.alio.alio_server.domain.user.service.implementation.UserReader;
import com.alio.alio_server.domain.user.service.implementation.UserUpdater;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommandAuthService {

    private final UserReader userReader;
    private final UserUpdater userUpdater;
    private final UserCreator userCreator;
    private final UserDeleter userDeleter;
    private final AuthReader authReader;
    private final AuthUpdater authUpdater;
    private final AuthDeleter authDeleter;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse signup(SignupRequest request) {
        userReader.findByUsername(request.username())
                .ifPresent(user -> {
                    throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
                });

        Users user = userCreator.signup(request.username(), request.password(), request.nickname());
        TokenResponse tokenResponse = authUpdater.publishToken(user, null);
        boolean detailCompleted = hasNickname(user);
        return new LoginResponse(tokenResponse, detailCompleted);
    }

    public LoginResponse login(LoginRequest request) {
        Users user = userReader.findByUsername(request.username())
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다."));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        Token existingToken = authReader.findByUser(user).orElse(null);
        TokenResponse tokenResponse = authUpdater.publishToken(user, existingToken);
        boolean detailCompleted = hasNickname(user);
        return new LoginResponse(tokenResponse, detailCompleted);
    }

    public void logout(TokenRefreshRequest request) {
        Token existingToken = authReader.findByRefreshToken(request.refreshToken());
        authDeleter.deleteRefreshToken(existingToken);
    }

    public void submitAdditionalInfo(DetailRequest req, Long id) {
        Users user = userReader.findById(id);
        userUpdater.updateNickname(user, req.nickname());
    }

    public void quitUser(Long id) {
        Users user = userReader.findById(id);
        userDeleter.delete(user);
    }

    private boolean hasNickname(Users user) {
        return user.getNickname() != null && !user.getNickname().isBlank();
    }
}
