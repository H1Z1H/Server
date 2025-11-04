package com.brick.brick_server.domain.user.exception;

import com.brick.brick_server.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class DailyMoneyAlreadyReceivedException extends BaseException {
    public DailyMoneyAlreadyReceivedException() {
        super(HttpStatus.TOO_MANY_REQUESTS, "DAILY_MONEY_ALREADY_RECEIVED", "오늘은 이미 용돈을 받았습니다.");
    }
}