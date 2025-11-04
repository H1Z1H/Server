package com.brick.brick_server.domain.user.presentation;


import com.brick.brick_server.domain.user.domain.Users;
import com.brick.brick_server.domain.user.presentation.dto.req.CoinTransactionRequest;
import com.brick.brick_server.domain.user.presentation.dto.req.UserClothRequest;
import com.brick.brick_server.domain.user.presentation.dto.req.WearClothRequest;
import com.brick.brick_server.domain.user.presentation.dto.res.BasicInfoResponse;
import com.brick.brick_server.domain.user.presentation.dto.res.CoinDetailResponse;
import com.brick.brick_server.domain.user.presentation.dto.res.UserRankingResponse;
import com.brick.brick_server.domain.user.service.CommandUserService;
import com.brick.brick_server.domain.user.service.QueryUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.brick.brick_server.common.util.AuthenticationUtil.getUserId;

@Tag(name = "유저")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final QueryUserService queryUserService;
    private final CommandUserService commandUserService;

    @Operation(summary = "회원 기본 정보 조회")
    @GetMapping("/basic-info")
//    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<BasicInfoResponse> getBasicInfo() {
        return ResponseEntity.ok(queryUserService.getBasicInfo(getUserId()));
    }
    @Operation(summary = "코인 매수/매도")
    @PostMapping("/coin")
    public ResponseEntity<Void> buyOrSellCoin(@RequestBody CoinTransactionRequest request) {
        commandUserService.buyOrSellCoin(request,getUserId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "코인 상세 조회")
    @GetMapping("/coin")
    public ResponseEntity<List<CoinDetailResponse>> getCoinDetail(@RequestParam String name) {
        return ResponseEntity.ok(queryUserService.getCoinDetail(name));
    }

    @Operation(summary = "유저 옷 등록")
    @PostMapping("/cloth")
    public ResponseEntity<Void> createUserCloth(@RequestBody UserClothRequest request) {
        commandUserService.createUserCloth(request, getUserId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "유저 착용 옷 변경")
    @PatchMapping("/cloth/wear")
    public ResponseEntity<Void> wearCloth(@RequestBody WearClothRequest request) {
        commandUserService.wearCloth(request, getUserId());
        return ResponseEntity.ok().build();
    }
    @Operation(summary = "유저 용돈 지급")
    @PostMapping("/money")
    public ResponseEntity<Void> addMoney() {
        commandUserService.addMoney(getUserId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "유저 순위 조회")
    @GetMapping("/rank")
    public ResponseEntity<List<UserRankingResponse>> getUsersByMoney() {
        return ResponseEntity.ok(queryUserService.getUserRankings());
    }
}
