package com.example.bojchallenge.global.common;

public class BadRequestException extends BusinessException {

    public BadRequestException(String message) {
        super(ErrorCode.BAD_REQUEST, message);
    }
}
