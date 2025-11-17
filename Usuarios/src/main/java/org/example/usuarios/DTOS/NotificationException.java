package org.example.usuarios.DTOS;

public class NotificationException extends RuntimeException {

    private final String errorCode;

    public NotificationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
