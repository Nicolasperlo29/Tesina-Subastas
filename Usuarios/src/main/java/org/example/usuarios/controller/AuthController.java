package org.example.usuarios.controller;

import org.example.usuarios.DTOS.*;
import org.example.usuarios.client.NotificationClient;
import org.example.usuarios.entity.UserEntity;
import org.example.usuarios.repository.UserRepository;
import org.example.usuarios.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationClient notificationClient;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        String token = authService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<UserEntity> crearUsuario(@RequestBody UserDTOPost userDTO) {
        return ResponseEntity.ok(authService.registerUser(userDTO));
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyUser(@RequestParam String token) {
        Optional<UserEntity> userOpt = userRepository.findByVerificationToken(token);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Token inválido");
        }

        UserEntity user = userOpt.get();
        user.setVerified(true);
        user.setActivo(true);
        user.setVerificationToken(null); // eliminar token
        userRepository.save(user);

        notificationClient.notifyBienvenidaEmail(userOpt.get().getEmail(), userOpt.get().getId());

        return ResponseEntity.ok("Usuario verificado correctamente");
    }

    @PostMapping("/activar")
    public ResponseEntity<UserEntity> activarCuenta(@RequestParam String email) {
        try {
            UserEntity user = authService.activarCuenta(email);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Solicitar recuperación de contraseña
    @PostMapping("/recover")
    public ResponseEntity<?> requestPasswordReset(@RequestBody EmailRequest request) {
        authService.requestPasswordReset(request.getEmail());
        return ResponseEntity.ok(Collections.singletonMap("message", "Se ha enviado un correo para restablecer la contraseña"));
    }

    // Cambiar la contraseña con el token
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(Collections.singletonMap("message", "Contraseña restablecida con éxito"));
    }

    @GetMapping("/pong")
    public String getString() {
        return "Hola";
    }
}
