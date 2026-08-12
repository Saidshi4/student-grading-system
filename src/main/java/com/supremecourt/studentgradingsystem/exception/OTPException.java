package com.supremecourt.studentgradingsystem.exception;

import lombok.Getter;

@Getter
public class OTPException extends RuntimeException {
    private final String message;
    private final String log;

    public OTPException(String message, String log) {
        this.message = message;
        this.log = log;
    }
}
