package com.brick.brick_server.domain.admin.presentation;

import com.brick.brick_server.domain.admin.domain.Article;
import com.brick.brick_server.domain.admin.presentation.dto.req.LoginRequest;
import com.brick.brick_server.domain.admin.presentation.dto.req.SetArticleTimeRequest;
import com.brick.brick_server.domain.admin.presentation.dto.req.UpdateArticleRequest;
import com.brick.brick_server.domain.admin.presentation.dto.res.ArticleContentResponse;
import com.brick.brick_server.domain.admin.presentation.dto.res.ArticleWithFluctuation;
import com.brick.brick_server.domain.admin.presentation.dto.res.CoinFluctuationResponse;
import com.brick.brick_server.domain.admin.service.CommandAdminService;
import com.brick.brick_server.domain.admin.service.QueryAdminService;
import com.brick.brick_server.domain.auth.presentation.dto.res.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "어드민")
@Slf4j
@RestController
@RequiredArgsConstructor
public class AdminController {
    private final CommandAdminService commandAdminService;
        private final QueryAdminService queryAdminService;

    @Operation(summary = "어드민 로그인")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginAdmin(@RequestBody LoginRequest req) {
        return ResponseEntity.ok(commandAdminService.loginAdmin(req));
    }

    @Operation(summary = "유저 기사 전체 조회")
    @GetMapping("/article")
    public ResponseEntity<List<Article>> getAllArticles() {
        return ResponseEntity.ok(queryAdminService.getAllArticles());
    }

    @Operation(summary = "어드민 기사 전체 조회")
    @GetMapping("/admin/article")
    public ResponseEntity<List<Article>> getAllAdminArticles() {
        return ResponseEntity.ok(queryAdminService.getAllAdminArticles());
    }


    @Operation(summary = "기사 상세 조회")
    @GetMapping("/article/detail/{articleId}")
    public ResponseEntity<ArticleContentResponse> getArticleDetail(@PathVariable Long articleId) {
        return ResponseEntity.ok(queryAdminService.getArticleDetail(articleId));
    }

    @Operation(summary = "기사 수정")
    @PatchMapping("/article")
    public ResponseEntity<Void> updateArticle(@RequestBody UpdateArticleRequest request) {
        commandAdminService.updateArticle(request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "기사 출판일 설정")
    @PostMapping("/article")
    public ResponseEntity<Void> setArticleTime(@RequestBody SetArticleTimeRequest request) {
        commandAdminService.setArticleTime(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "기사 발행")
    @PatchMapping("/article/publish/{articleId}")
    public ResponseEntity<Void> publishArticle(@PathVariable Long articleId) {
        commandAdminService.publishArticle(articleId);
        return ResponseEntity.ok().build();
    }
    @Operation(summary = "LLM 기사 생성 요청")
    @PostMapping("/article/ai")
    public ResponseEntity<Void> generateNewsWithCoinImpact() {
        commandAdminService.generateNewsWithCoinImpact();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "모든 코인의 가격 변동률 조회")
    @GetMapping("/fluctuation")
    public ResponseEntity<List<CoinFluctuationResponse>> getAllCoinFluctuations() {
        return ResponseEntity.ok(queryAdminService.getAllCoinFluctuations());
    }
}
