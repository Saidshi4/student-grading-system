package com.supremecourt.studentgradingsystem.exception;

import lombok.Getter;

@Getter
public class UserAlreadyExistsException extends RuntimeException {
    private final String message;
    private final String log;
    public UserAlreadyExistsException(String message, String log) {
        super(message);
        this.message = message;
        this.log = log;
    }
}
