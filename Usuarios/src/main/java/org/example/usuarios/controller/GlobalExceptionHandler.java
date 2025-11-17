package org.example.usuarios.controller;

import org.example.usuarios.DTOS.UsuarioYaExistenteException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsuarioYaExistenteException.class)
    public ResponseEntity<Map<String, Object>> handleUsuarioYaExistente(UsuarioYaExistenteException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getMessage());
        body.put("errorCode", ex.getErrorCode());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }
}
