package com.brick.brick_server.domain.admin.service.implementation;

import com.brick.brick_server.common.jwt.Jwt;
import com.brick.brick_server.common.jwt.dto.TokenResponse;
import com.brick.brick_server.domain.admin.domain.Admin;
import com.brick.brick_server.domain.admin.domain.Article;
import com.brick.brick_server.domain.admin.domain.repository.AdminRepository;
import com.brick.brick_server.domain.admin.domain.repository.ArticleRepository;
import com.brick.brick_server.domain.admin.presentation.dto.req.LoginRequest;
import com.brick.brick_server.domain.admin.presentation.dto.req.SetArticleTimeRequest;
import com.brick.brick_server.domain.admin.presentation.dto.req.UpdateArticleRequest;
import com.brick.brick_server.domain.auth.presentation.dto.res.LoginResponse;
import com.brick.brick_server.domain.user.domain.vo.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.brick.brick_server.domain.admin.exception.*;

import java.util.List;
import java.util.NoSuchElementException;


@Service
@RequiredArgsConstructor
public class AdminCreator {

    private final Jwt jwt;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest req) {
        Admin admin = adminRepository.findByUsername(req.id())
                .orElseThrow(AdminNotFoundException::new);

        if (admin.getRole() != Role.ADMIN) {
            throw new NotAdminRoleException();
        }

        if (!passwordEncoder.matches(req.password(), admin.getPassword())) {
            throw new InvalidPasswordException();
        }

        Jwt.Claims claims = Jwt.Claims.from(admin.getId(), new Role[]{Role.USER});
        TokenResponse tokenResponse = jwt.generateAllToken(claims);

        admin.updateRefreshToken(tokenResponse.refreshToken());
        adminRepository.save(admin);

        return new LoginResponse(tokenResponse, true);
    }
}