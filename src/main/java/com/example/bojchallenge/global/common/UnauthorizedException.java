package com.example.bojchallenge.global.common;

public class UnauthorizedException extends BusinessException {

    public UnauthorizedException(String message) {
        super(ErrorCode.UNAUTHORIZED, message);
    }
}
