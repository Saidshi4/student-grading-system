package com.supremecourt.studentgradingsystem.exception;

import lombok.Getter;

@Getter
public class FirebaseException extends RuntimeException {
    private final String message;
    private final String log;

    public FirebaseException(String message, String log) {
        this.message = message;
        this.log = log;
    }
}