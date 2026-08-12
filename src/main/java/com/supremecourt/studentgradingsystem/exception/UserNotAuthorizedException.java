package com.supremecourt.studentgradingsystem.exception;

import lombok.Getter;

@Getter
public class UserNotAuthorizedException extends RuntimeException {
    private final String message;
    private final String log;
    public UserNotAuthorizedException(String message, String log) {
        super(message);
        this.message = message;
        this.log = log;
    }
}
