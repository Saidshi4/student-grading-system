package com.supremecourt.studentgradingsystem.handler;

import com.supremecourt.studentgradingsystem.exception.IsNotEmptyException;
import com.supremecourt.studentgradingsystem.exception.JwtExpiredException;
import com.supremecourt.studentgradingsystem.exception.NotFoundException;
import com.supremecourt.studentgradingsystem.exception.NotPermission;
import com.supremecourt.studentgradingsystem.exception.OTPException;
import com.supremecourt.studentgradingsystem.exception.PasswordResetException;
import com.supremecourt.studentgradingsystem.exception.PermissionException;
import com.supremecourt.studentgradingsystem.exception.UserAlreadyExistsException;
import com.supremecourt.studentgradingsystem.exception.UserAlreadyStaffException;
import com.supremecourt.studentgradingsystem.exception.UserNotAuthorizedException;
import com.supremecourt.studentgradingsystem.model.response.ExceptionResponseDto;
import com.supremecourt.studentgradingsystem.model.response.ExceptionResponseDtoWithoutLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponseDto handle(NotFoundException e) {
        return logAndBuild(e.getLog(), e.getMessage());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionResponseDto handle(UserAlreadyExistsException e) {
        return logAndBuild(e.getLog(), e.getMessage());
    }

    @ExceptionHandler(UserAlreadyStaffException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionResponseDto handle(UserAlreadyStaffException e) {
        return logAndBuild(e.getLog(), e.getMessage());
    }

    @ExceptionHandler(PermissionException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ExceptionResponseDto handle(PermissionException e) {
        return logAndBuild(e.getLog(), e.getMessage());
    }

    @ExceptionHandler(UserNotAuthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ExceptionResponseDto handle(UserNotAuthorizedException e) {
        return logAndBuild(e.getLog(), e.getMessage());
    }

    @ExceptionHandler(OTPException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponseDto handle(OTPException e) {
        return logAndBuild(e.getLog(), e.getMessage());
    }

    @ExceptionHandler(PasswordResetException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponseDto handle(PasswordResetException e) {
        return logAndBuild(e.getLog(), e.getMessage());
    }

    @ExceptionHandler(IsNotEmptyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponseDto handle(IsNotEmptyException e) {
        return logAndBuild(e.getLog(), e.getMessage());
    }

    @ExceptionHandler(NotPermission.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ExceptionResponseDto handle(NotPermission e) {
        return logAndBuild(e.getLog(), e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponseDtoWithoutLog handle(IllegalArgumentException e) {
        return logAndBuild(e.getMessage());
    }

    @ExceptionHandler(JwtExpiredException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ExceptionResponseDto handle(JwtExpiredException e) {
        return logAndBuild(e.getLog(), e.getMessage());
    }


    private ExceptionResponseDto logAndBuild(String logMessage, String responseMessage) {
        if (logMessage != null && !logMessage.isBlank()) {
            log.error(logMessage);
        }
        return new ExceptionResponseDto(responseMessage);
    }

    private ExceptionResponseDtoWithoutLog logAndBuild(String responseMessage) {
        return new ExceptionResponseDtoWithoutLog(responseMessage);
    }
}
