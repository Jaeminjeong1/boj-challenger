package com.example.bojchallenge.global.common;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "BAD_REQUEST"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "FORBIDDEN"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "NOT_FOUND"),
    CONFLICT(HttpStatus.CONFLICT, "CONFLICT"),
    TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS, "TOO_MANY_REQUESTS"),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR");

    private final HttpStatus status;
    private final String code;

    ErrorCode(HttpStatus status, String code) {
        this.status = status;
        this.code = code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }
}
