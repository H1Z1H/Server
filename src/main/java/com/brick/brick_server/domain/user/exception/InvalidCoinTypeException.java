package com.brick.brick_server.domain.user.exception;

import com.brick.brick_server.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidCoinTypeException extends BaseException {
    public InvalidCoinTypeException() {
        super(HttpStatus.BAD_REQUEST, "INVALID_COIN_TYPE", "type은 buy 또는 sell이어야 합니다.");
    }
}