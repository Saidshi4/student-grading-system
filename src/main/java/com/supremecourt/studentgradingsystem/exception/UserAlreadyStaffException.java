package com.supremecourt.studentgradingsystem.exception;

import lombok.Getter;

@Getter
public class UserAlreadyStaffException extends RuntimeException {
    private final String message;
    private final String log;
    public UserAlreadyStaffException(String message, String log) {
        super(message);
        this.message = message;
        this.log = log;
    }
}
