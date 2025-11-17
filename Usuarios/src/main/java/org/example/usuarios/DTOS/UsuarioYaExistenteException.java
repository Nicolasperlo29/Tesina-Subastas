package org.example.usuarios.DTOS;

public class UsuarioYaExistenteException extends RuntimeException {

    private final String errorCode;

    public UsuarioYaExistenteException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
