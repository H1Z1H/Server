package com.brick.brick_server.domain.auth.service;


import com.brick.brick_server.common.jwt.dto.TokenResponse;
import com.brick.brick_server.domain.auth.domain.Token;
import com.brick.brick_server.domain.auth.infra.dto.SocialPlatformUserInfo;
import com.brick.brick_server.domain.auth.infra.oauth.KakaoClient;
import com.brick.brick_server.domain.auth.presentation.dto.req.DetailRequest;
import com.brick.brick_server.domain.auth.presentation.dto.req.TokenRefreshRequest;
import com.brick.brick_server.domain.auth.presentation.dto.res.LoginResponse;
import com.brick.brick_server.domain.auth.service.implementation.AuthDeleter;
import com.brick.brick_server.domain.auth.service.implementation.AuthReader;
import com.brick.brick_server.domain.auth.service.implementation.AuthUpdater;
import com.brick.brick_server.domain.user.domain.Users;
import com.brick.brick_server.domain.user.service.implementation.UserCreator;
import com.brick.brick_server.domain.user.service.implementation.UserDeleter;
import com.brick.brick_server.domain.user.service.implementation.UserReader;
import com.brick.brick_server.domain.user.service.implementation.UserUpdater;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional
public class CommandAuthService {
    private final KakaoClient kakaoClient;
    private final UserReader userReader;
    private final UserUpdater userUpdater;
    private final UserCreator userCreator;
    private final UserDeleter userDeleter;
    private final AuthReader authReader;
    private final AuthUpdater authUpdater;
    private final AuthDeleter authDeleter;

    public LoginResponse login(String code){
        SocialPlatformUserInfo userInfo = kakaoClient.getUserInfo(kakaoClient.getAccessToken(code));

        Optional<Users> user = userReader.findBySocialAccountUid(userInfo.userId());

        if(user.isPresent()) {
            Users existingUser = user.get();
            Token token = authReader.findByUser(user);
            TokenResponse tokenResponse = authUpdater.refreshToken(token);

            boolean detail = existingUser.getGender() != null;


            return new LoginResponse(tokenResponse,detail);
        } else {
            Users signupUser = userCreator.signup(userInfo.userId());
            TokenResponse tokenResponse = authUpdater.publishToken(signupUser, null);

            return new LoginResponse(tokenResponse, false);
        }
    }

    public void logout(TokenRefreshRequest request){
        Token existingToken = authReader.findByRefreshToken(request.refreshToken());
        authDeleter.deleteRefreshToken(existingToken);
    }

    public void submitAdditionalInfo(DetailRequest req, Long id){
        Users user = userReader.findById(id);
        userUpdater.updateDetail(user, req.nickname(),req.gender());
    }

    public void quitUser(Long id){
        Users user = userReader.findById(id);
        userDeleter.delete(user);
    }
}
