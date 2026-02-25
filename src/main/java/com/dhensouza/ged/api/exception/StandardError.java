package com.dhensouza.ged.api.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record StandardError(
        int status,
        String error,
        String message,
        Instant timestamp,
        String path,
        List<ValidationError> errors
) {
    public record ValidationError(String field, String message) {}
}