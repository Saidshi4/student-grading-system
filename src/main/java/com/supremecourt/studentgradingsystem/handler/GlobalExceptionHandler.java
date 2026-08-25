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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionResponseDto> handle(NotFoundException e, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, e.getLog(), e.getMessage(), request);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ExceptionResponseDto> handle(UserAlreadyExistsException e, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, e.getLog(), e.getMessage(), request);
    }

    @ExceptionHandler(UserAlreadyStaffException.class)
    public ResponseEntity<ExceptionResponseDto> handle(UserAlreadyStaffException e, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, e.getLog(), e.getMessage(), request);
    }

    @ExceptionHandler(PermissionException.class)
    public ResponseEntity<ExceptionResponseDto> handle(PermissionException e, HttpServletRequest request) {
        return build(HttpStatus.FORBIDDEN, e.getLog(), e.getMessage(), request);
    }

    @ExceptionHandler(UserNotAuthorizedException.class)
    public ResponseEntity<ExceptionResponseDto> handle(UserNotAuthorizedException e, HttpServletRequest request) {
        return build(HttpStatus.UNAUTHORIZED, e.getLog(), e.getMessage(), request);
    }

    @ExceptionHandler(OTPException.class)
    public ResponseEntity<ExceptionResponseDto> handle(OTPException e, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, e.getLog(), e.getMessage(), request);
    }

    @ExceptionHandler(PasswordResetException.class)
    public ResponseEntity<ExceptionResponseDto> handle(PasswordResetException e, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, e.getLog(), e.getMessage(), request);
    }

    @ExceptionHandler(IsNotEmptyException.class)
    public ResponseEntity<ExceptionResponseDto> handle(IsNotEmptyException e, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, e.getLog(), e.getMessage(), request);
    }

    @ExceptionHandler(NotPermission.class)
    public ResponseEntity<ExceptionResponseDto> handle(NotPermission e, HttpServletRequest request) {
        return build(HttpStatus.FORBIDDEN, e.getLog(), e.getMessage(), request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponseDto> handle(IllegalArgumentException e, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, e.getMessage(), e.getMessage(), request);
    }

    @ExceptionHandler(JwtExpiredException.class)
    public ResponseEntity<ExceptionResponseDto> handle(JwtExpiredException e, HttpServletRequest request) {
        return build(HttpStatus.UNAUTHORIZED, e.getLog(), e.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponseDto> handle(MethodArgumentNotValidException e, HttpServletRequest request) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        if (message.isBlank()) {
            message = "Validation failed";
        }
        return build(HttpStatus.BAD_REQUEST, message, message, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponseDto> handle(ConstraintViolationException e, HttpServletRequest request) {
        String message = e.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + " " + violation.getMessage())
                .collect(Collectors.joining("; "));
        return build(HttpStatus.BAD_REQUEST, message, message, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponseDto> handle(HttpMessageNotReadableException e, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, e.getMessage(), "Invalid request body", request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ExceptionResponseDto> handle(DataIntegrityViolationException e, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, e.getMostSpecificCause().getMessage(), "Duplicate or conflicting data", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponseDto> handle(Exception e, HttpServletRequest request) {
        log.error("Unhandled exception on {}", request.getRequestURI(), e);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), "Internal server error", request);
    }

    private ResponseEntity<ExceptionResponseDto> build(HttpStatus status, String logMessage, String responseMessage,
                                                       HttpServletRequest request) {
        if (logMessage != null && !logMessage.isBlank()) {
            log.error(logMessage);
        }
        ExceptionResponseDto body = ExceptionResponseDto.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(status.name())
                .message(responseMessage)
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(status).body(body);
    }
}
