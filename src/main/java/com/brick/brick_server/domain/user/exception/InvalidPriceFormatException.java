package com.brick.brick_server.domain.user.exception;

import com.brick.brick_server.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidPriceFormatException extends BaseException {
    public InvalidPriceFormatException(String price) {
        super(HttpStatus.BAD_REQUEST, "INVALID_PRICE_FORMAT", "price는 숫자 형식이어야 합니다. 입력값: " + price);
    }
}