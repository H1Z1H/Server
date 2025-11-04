package com.brick.brick_server.domain.admin.exception;

import com.brick.brick_server.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class AdminNotFoundException extends BaseException {
    public AdminNotFoundException() {
        super(HttpStatus.NOT_FOUND, "ADMIN_NOT_FOUND", "존재하지 않는 관리자입니다.");
    }
}