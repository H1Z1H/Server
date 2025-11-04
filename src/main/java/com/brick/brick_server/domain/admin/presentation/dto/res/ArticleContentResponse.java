package com.brick.brick_server.domain.admin.presentation.dto.res;

public record ArticleContentResponse(
        String content
) {
    public static ArticleContentResponse from(String content) {
        return new ArticleContentResponse(content);
    }
}