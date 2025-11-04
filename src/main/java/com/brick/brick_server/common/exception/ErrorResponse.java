package com.brick.brick_server.common.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private final HttpStatus status;
    private final String errorCode;
    private final String message;
    private final String path;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private final LocalDateTime time;
    private final List<FieldError> inputErrors;

    @Getter
    @AllArgsConstructor
    public static class FieldError {
        private final String field;
        private final Object rejectedValue;
        private final String message;
    }

    public static ErrorResponse of(HttpStatus status, String errorCode, String message, String path) {
        return new ErrorResponse(status, errorCode, message, path, LocalDateTime.now(), null);
    }

    public static ErrorResponse of(HttpStatus status, String errorCode, String message, String path, List<FieldError> inputErrors) {
        return new ErrorResponse(status, errorCode, message, path, LocalDateTime.now(), inputErrors);
    }
}
