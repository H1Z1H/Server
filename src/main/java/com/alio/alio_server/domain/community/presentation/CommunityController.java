package com.alio.alio_server.domain.community.presentation;

import com.alio.alio_server.domain.community.presentation.dto.req.CreateCommunityPostRequest;
import com.alio.alio_server.domain.community.presentation.dto.res.CaseArchiveResponse;
import com.alio.alio_server.domain.community.presentation.dto.res.CommunityPostCardResponse;
import com.alio.alio_server.domain.community.presentation.dto.res.CommunityPostListResponse;
import com.alio.alio_server.domain.community.presentation.dto.res.CommunityPostResponse;
import com.alio.alio_server.domain.community.service.CommandCommunityService;
import com.alio.alio_server.domain.community.service.QueryCommunityService;
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
@RequestMapping("/community")
@RequiredArgsConstructor
@Tag(name = "커뮤니티")
public class CommunityController {

    private final CommandCommunityService commandCommunityService;
    private final QueryCommunityService queryCommunityService;

    @Operation(summary = "커뮤니티 게시글 작성")
    @PostMapping("/posts")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<CommunityPostResponse> createPost(
            @Valid @RequestBody CreateCommunityPostRequest request
    ) {
        Long userId = getUserId();
        return ResponseEntity.ok(commandCommunityService.createPost(userId, request));
    }

    @Operation(summary = "커뮤니티 게시글 목록 조회 (리스트형)")
    @GetMapping("/posts")
    public ResponseEntity<List<CommunityPostListResponse>> getPosts(
            @RequestParam(required = false) String tag
    ) {
        return ResponseEntity.ok(queryCommunityService.getPosts(tag));
    }

    @Operation(summary = "커뮤니티 게시글 카드 조회 (요약형)")
    @GetMapping("/posts/cards")
    public ResponseEntity<List<CommunityPostCardResponse>> getCards(
            @RequestParam(defaultValue = "6") int limit
    ) {
        return ResponseEntity.ok(queryCommunityService.getCards(limit));
    }

    @Operation(summary = "커뮤니티 게시글 상세 조회")
    @GetMapping("/posts/{postId}")
    public ResponseEntity<CommunityPostResponse> getPost(
            @PathVariable Long postId
    ) {
        return ResponseEntity.ok(queryCommunityService.getPost(postId));
    }

    @Operation(summary = "케이스 아카이브 조회")
    @GetMapping("/cases")
    public ResponseEntity<List<CaseArchiveResponse>> getCaseArchive() {
        return ResponseEntity.ok(queryCommunityService.getCaseArchive());
    }
}

