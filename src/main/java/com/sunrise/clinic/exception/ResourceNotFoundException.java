package com.sunrise.clinic.exception;

/** Thrown when a record requested by the user does not exist. */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
