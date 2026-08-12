package com.supremecourt.studentgradingsystem.exception;

import lombok.Getter;

@Getter
public class PasswordResetException extends RuntimeException {
    private final String message;
    private final String log;

    public PasswordResetException(String message, String log) {
        this.message = message;
        this.log = log;
    }
}
