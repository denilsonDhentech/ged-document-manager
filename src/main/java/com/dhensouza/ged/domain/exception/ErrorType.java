package com.dhensouza.ged.domain.exception;

public enum ErrorType {
    ENTITY_NOT_FOUND("The requested resource was not found"),
    BUSINESS_RULE_VIOLATION("A business rule has been violated"),
    ACCESS_DENIED("You do not have permission for this action"),
    INTERNAL_SERVER_ERROR("An unexpected error occurred"),
    INVALID_PARAMETERS("Invalid fields in request");

    private final String message;

    ErrorType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
