package com.dhensouza.ged.api.exception;

import com.dhensouza.ged.domain.exception.BusinessRuleException;
import com.dhensouza.ged.domain.exception.DomainException;
import com.dhensouza.ged.domain.exception.EntityNotFoundException;
import com.dhensouza.ged.domain.exception.ErrorType;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class)
    public ResponseEntity<StandardError> handleTypeMismatch(
            org.springframework.web.method.annotation.MethodArgumentTypeMismatchException e,
            HttpServletRequest request) {

        String message = String.format("Parameter '%s' has an invalid value. Expected type: %s",
                e.getName(), e.getRequiredType().getSimpleName());

        return buildResponse(HttpStatus.BAD_REQUEST, ErrorType.INVALID_PARAMETERS, message, request, null);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<StandardError> entityNotFound(EntityNotFoundException e, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ErrorType.ENTITY_NOT_FOUND, e.getMessage(), request, null);
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<StandardError> businessRule(BusinessRuleException e, HttpServletRequest request) {
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, ErrorType.BUSINESS_RULE_VIOLATION, e.getMessage(), request, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardError> validationError(MethodArgumentNotValidException e, HttpServletRequest request) {
        List<StandardError.ValidationError> validationErrors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(f -> new StandardError.ValidationError(f.getField(), f.getDefaultMessage()))
                .toList();

        return buildResponse(HttpStatus.BAD_REQUEST, ErrorType.INVALID_PARAMETERS,
                ErrorType.INVALID_PARAMETERS.getMessage(), request, validationErrors);
    }

    private ResponseEntity<StandardError> buildResponse(HttpStatus status, ErrorType type, String message,
                                                        HttpServletRequest request, List<StandardError.ValidationError> errors) {
        StandardError err = new StandardError(
                status.value(),
                type.name(),
                message,
                Instant.now(),
                request.getRequestURI(),
                errors
        );
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardError> handleUncaught(Exception e, HttpServletRequest request) {
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ErrorType.INTERNAL_SERVER_ERROR,
                "An unexpected internal error occurred",
                request,
                null
        );
    }
}