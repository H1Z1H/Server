package com.alio.alio_server.domain.news.presentation.dto.res;

import com.alio.alio_server.domain.news.domain.NewsComment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NewsCommentResponse {
    private final Long id;
    private final String content;
    private final LocalDateTime createdAt;

    public static NewsCommentResponse from(NewsComment comment) {
        return NewsCommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}

