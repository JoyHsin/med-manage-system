package org.me.joy.clinic.exception;

public class ValidationException extends ClinicManagementException {
    public ValidationException(String errorCode, String message) {
        super(errorCode, message);
    }

    public ValidationException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}