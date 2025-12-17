package com.dscommerce.controllers.exceptions.handler;

import com.dscommerce.controllers.exceptions.StandardError;
import com.dscommerce.dto.exceptions.ValidationError;
import com.dscommerce.services.exceptions.DatabaseException;
import com.dscommerce.services.exceptions.ForbiddenException;
import com.dscommerce.services.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class ResourceExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardError> resourceNotFound(
            ResourceNotFoundException e,
            HttpServletRequest request) {
        String error = "Resource not found";
        HttpStatus status = HttpStatus.NOT_FOUND;
        StandardError err = new StandardError(
                Instant.now(),
                status.value(),
                error,
                e.getMessage(),
                request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<StandardError> database(
            DatabaseException e,
            HttpServletRequest request) {
        String error = "Database error";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        StandardError err = new StandardError(
                Instant.now(),
                status.value(),
                error,
                e.getMessage(),
                request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> methodArgumentNotValid(
            MethodArgumentNotValidException e,
            HttpServletRequest request) {
        String error = "Invalid data";
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        ValidationError validationError = new ValidationError(
                Instant.now(),
                status.value(),
                error,
                request.getRequestURI());
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            validationError.addError(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return ResponseEntity.status(status).body(validationError);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<StandardError> forbidden(
            ForbiddenException e,
            HttpServletRequest request) {
        String error = "Unauthorized";
        HttpStatus status = HttpStatus.FORBIDDEN;
        StandardError err = new StandardError(
                Instant.now(),
                status.value(),
                error,
                e.getMessage(),
                request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }
}
