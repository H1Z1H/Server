package com.brick.brick_server.domain.admin.exception;

import com.brick.brick_server.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ArticleNotFoundException extends BaseException {
    public ArticleNotFoundException() {
        super(HttpStatus.NOT_FOUND, "ARTICLE_NOT_FOUND", "해당 기사를 찾을 수 없습니다.");
    }
}