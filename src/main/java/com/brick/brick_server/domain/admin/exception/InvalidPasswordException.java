package com.brick.brick_server.domain.admin.exception;

import com.brick.brick_server.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidPasswordException extends BaseException {
    public InvalidPasswordException() {
        super(HttpStatus.UNAUTHORIZED, "INVALID_PASSWORD", "비밀번호가 일치하지 않습니다.");
    }
}