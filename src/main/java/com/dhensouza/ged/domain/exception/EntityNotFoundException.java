package com.dhensouza.ged.domain.exception;

public class EntityNotFoundException extends DomainException {
    public EntityNotFoundException(String message) {
        super(ErrorType.ENTITY_NOT_FOUND, message);
    }
}