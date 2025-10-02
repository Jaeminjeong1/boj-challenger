package com.example.bojchallenge.global.common;

public class ForbiddenException extends BusinessException {

    public ForbiddenException(String message) {
        super(ErrorCode.FORBIDDEN, message);
    }
}
