package com.alio.alio_server.domain.auth.presentation;


import com.alio.alio_server.domain.auth.presentation.dto.req.DetailRequest;
import com.alio.alio_server.domain.auth.presentation.dto.req.LoginRequest;
import com.alio.alio_server.domain.auth.presentation.dto.req.SignupRequest;
import com.alio.alio_server.domain.auth.presentation.dto.req.TokenRefreshRequest;
import com.alio.alio_server.domain.auth.presentation.dto.res.LoginResponse;
import com.alio.alio_server.domain.auth.service.CommandAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.alio.alio_server.common.util.AuthenticationUtil.getUserId;


@Tag(name = "인증/인가")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final CommandAuthService commandAuthService;

    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    public ResponseEntity<LoginResponse> signup(@Valid @RequestBody SignupRequest request) {
        return ResponseEntity.ok(commandAuthService.signup(request));
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(commandAuthService.login(request));
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<Void> logout(@Valid @RequestBody TokenRefreshRequest req) {
        commandAuthService.logout(req);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "상세 정보 요청")
    @PatchMapping("/detail")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<Void> submitAdditionalInfo(@Valid @RequestBody DetailRequest req) {
        commandAuthService.submitAdditionalInfo(req,getUserId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "회원 탈퇴")
    @DeleteMapping("/quit")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<Void> quitUser() {
        commandAuthService.quitUser(getUserId());
        return ResponseEntity.noContent().build();
    }
}