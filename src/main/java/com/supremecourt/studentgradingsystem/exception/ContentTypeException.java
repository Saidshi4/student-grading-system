package com.supremecourt.studentgradingsystem.exception;

import lombok.Getter;

@Getter
public class ContentTypeException extends RuntimeException {
    private final String message;
    private final String log;

    public ContentTypeException(String message, String log) {
        this.message = message;
        this.log = log;
    }
}
