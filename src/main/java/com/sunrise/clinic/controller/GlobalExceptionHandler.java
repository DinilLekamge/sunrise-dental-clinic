package com.sunrise.clinic.controller;

import com.sunrise.clinic.exception.BusinessRuleException;
import com.sunrise.clinic.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Turns application exceptions into a friendly page. Technical stack traces are
 * never shown to the user.
 */
@ControllerAdvice(basePackages = "com.sunrise.clinic.controller")
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(ResourceNotFoundException exception, Model model) {
        model.addAttribute("errorTitle", "Record not found");
        model.addAttribute("errorMessage", exception.getMessage());
        return "error";
    }

    @ExceptionHandler(BusinessRuleException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBusinessRule(BusinessRuleException exception, Model model) {
        model.addAttribute("errorTitle", "Action not allowed");
        model.addAttribute("errorMessage", exception.getMessage());
        return "error";
    }
}
