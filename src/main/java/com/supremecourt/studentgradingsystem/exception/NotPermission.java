package com.supremecourt.studentgradingsystem.exception;

import lombok.Getter;

@Getter
public class NotPermission extends RuntimeException {
    private final String message;
    private final String log;

    public NotPermission(String message, String log) {
        this.message = message;
        this.log = log;
    }
}
