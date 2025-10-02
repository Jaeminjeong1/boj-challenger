package com.example.bojchallenge.global.common;

public class ConflictException extends BusinessException {

    public ConflictException(String message) {
        super(ErrorCode.CONFLICT, message);
    }
}
