package com.sunrise.clinic.exception;

/** Thrown when an action would break one of the clinic business rules. */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
