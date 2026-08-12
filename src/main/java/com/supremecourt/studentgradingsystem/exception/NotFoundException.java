package com.supremecourt.studentgradingsystem.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {
    private final String message;
    private final String log;

    public NotFoundException(String message, String log) {
        this.message = message;
        this.log = log;
    }
}