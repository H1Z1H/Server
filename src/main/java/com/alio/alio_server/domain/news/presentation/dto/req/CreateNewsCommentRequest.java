package com.alio.alio_server.domain.news.presentation.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateNewsCommentRequest(
        @NotBlank
        @Size(max = 500, message = "댓글은 최대 500자까지 입력 가능합니다.")
        String content
) {
}

