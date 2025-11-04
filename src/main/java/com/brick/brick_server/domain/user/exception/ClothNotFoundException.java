package com.brick.brick_server.domain.user.exception;

import com.brick.brick_server.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ClothNotFoundException extends BaseException {
    public ClothNotFoundException() {
        super(HttpStatus.NOT_FOUND, "CLOTH_NOT_FOUND", "해당 이름의 옷이 존재하지 않습니다.");
    }
}