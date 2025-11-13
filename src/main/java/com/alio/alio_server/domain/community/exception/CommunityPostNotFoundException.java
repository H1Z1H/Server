package com.alio.alio_server.domain.community.exception;

import com.alio.alio_server.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CommunityPostNotFoundException extends BaseException {
    public CommunityPostNotFoundException() {
        super(HttpStatus.NOT_FOUND, "COMMUNITY_POST_NOT_FOUND", "커뮤니티 게시글을 찾을 수 없습니다.");
    }
}

