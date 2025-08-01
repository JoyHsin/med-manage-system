package org.me.joy.clinic.exception;

public class BusinessException extends ClinicManagementException {
    public BusinessException(String errorCode, String message) {
        super(errorCode, message);
    }

    public BusinessException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}