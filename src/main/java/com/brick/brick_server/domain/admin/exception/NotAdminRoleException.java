package com.brick.brick_server.domain.admin.exception;

import com.brick.brick_server.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class NotAdminRoleException extends BaseException {
    public NotAdminRoleException() {
        super(HttpStatus.FORBIDDEN, "NOT_ADMIN_ROLE", "관리자 권한이 없습니다.");
    }
}