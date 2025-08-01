package org.me.joy.clinic.exception;

public class ClinicManagementException extends RuntimeException {
    private final String errorCode;

    public ClinicManagementException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ClinicManagementException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}