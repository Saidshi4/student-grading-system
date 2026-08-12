package com.supremecourt.studentgradingsystem.exception;

import lombok.Getter;
import org.springframework.security.access.AccessDeniedException;

@Getter
public class PermissionException extends AccessDeniedException {
    private final String log;
    private final String message;
    public PermissionException(String message, String log) {
        super(message);
        this.message = message;
        this.log = log;
    }
}
