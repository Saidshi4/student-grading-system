package com.supremecourt.studentgradingsystem.exception;

import lombok.Getter;

@Getter
public class IsNotEmptyException extends RuntimeException {
    private final String message;
    private final String log;

    public IsNotEmptyException(String message, String log) {
        this.message = message;
        this.log = log;
    }
}
