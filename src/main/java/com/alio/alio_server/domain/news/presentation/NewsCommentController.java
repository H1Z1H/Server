package com.alio.alio_server.domain.news.presentation;

import com.alio.alio_server.domain.news.presentation.dto.req.CreateNewsCommentRequest;
import com.alio.alio_server.domain.news.presentation.dto.res.NewsCommentResponse;
import com.alio.alio_server.domain.news.service.CommandNewsCommentService;
import com.alio.alio_server.domain.news.service.QueryNewsCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.alio.alio_server.common.util.AuthenticationUtil.getUserId;

@RestController
@RequestMapping("/news/comments")
@RequiredArgsConstructor
@Tag(name = "뉴스 댓글")
public class NewsCommentController {

    private final CommandNewsCommentService commandNewsCommentService;
    private final QueryNewsCommentService queryNewsCommentService;

    @Operation(summary = "뉴스 댓글 작성")
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<NewsCommentResponse> createComment(
            @Valid @RequestBody CreateNewsCommentRequest request
    ) {
        Long userId = getUserId();
        return ResponseEntity.ok(commandNewsCommentService.createComment(userId, request));
    }

    @Operation(summary = "최근 뉴스 댓글 조회")
    @GetMapping("/recent")
    public ResponseEntity<List<NewsCommentResponse>> getRecentComments(
            @RequestParam(defaultValue = "3") int limit
    ) {
        return ResponseEntity.ok(queryNewsCommentService.getRecentComments(limit));
    }
}

