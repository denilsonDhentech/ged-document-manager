package com.dhensouza.ged.api.exception;

import com.dhensouza.ged.domain.exception.BusinessRuleException;
import com.dhensouza.ged.domain.exception.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<StandardError> businessRule(BusinessRuleException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        StandardError err = new StandardError(
                status.value(),
                e.getMessage(),
                Instant.now(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<StandardError> entityNotFound(EntityNotFoundException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        StandardError err = new StandardError(
                status.value(),
                e.getMessage(),
                Instant.now(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(err);
    }
}