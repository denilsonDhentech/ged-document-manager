package com.dhensouza.ged.domain.exception;

public class BusinessRuleException extends DomainException {
    public BusinessRuleException(String message) {
        super(ErrorType.BUSINESS_RULE_VIOLATION, message);
    }
}
