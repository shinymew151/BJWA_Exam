package com.fptaptech.employeemanagement.exception;

public class DuplicateEmailException extends RuntimeException {
    
    public DuplicateEmailException(String email) {
        super("An employee with email '" + email + "' already exists in the system.");
    }
    
    public DuplicateEmailException(String message, Throwable cause) {
        super(message, cause);
    }
}