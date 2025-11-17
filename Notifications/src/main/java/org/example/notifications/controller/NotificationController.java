package org.example.notifications.controller;

import org.example.notifications.DTO.EmailFinalizadaRequest;
import org.example.notifications.DTO.EmailRequest;
import org.example.notifications.DTO.NotificationDTO;
import org.example.notifications.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/email/ganador/{email}")
    public ResponseEntity<?> notificarEmailGanador(@PathVariable String email, @RequestBody Map<String, Long> body) {
        Long userId = body.get("userId");
        Long subastaId = body.get("subastaId");
        notificationService.sendGanadorEmail(email, userId, subastaId);
        return ResponseEntity.ok(Collections.singletonMap("message", "Correo enviado"));
    }

    @PostMapping("/email/subasta-creada/{email}")
    public ResponseEntity<?> notificarSubastaCreada(@PathVariable String email, @RequestBody Map<String, Long> body) {
        Long userId = body.get("userId");
        Long subastaId = body.get("subastaId");
        notificationService.sendSubastaCreada(email, userId, subastaId);
        return ResponseEntity.ok(Collections.singletonMap("message", "Correo enviado"));
    }

//    @PostMapping("/email/verification/{email}")
//    public ResponseEntity<?> notificarVerificacion(@PathVariable String email, @RequestParam String link, @RequestBody Map<String, Long> body) {
//        Long userId = body.get("userId");
//        notificationService.sendVerificationEmail(email, userId, link);
//        return ResponseEntity.ok(Collections.singletonMap("message", "Correo de verificación enviado"));
//    }

    @PostMapping("/verification/email/{email}")
    public ResponseEntity<?> notificarVerificacion(@RequestBody EmailRequest request) {
        notificationService.sendVerificationEmail(request.getEmail(), request.getUserId(), request.getLink());
        return ResponseEntity.ok(Collections.singletonMap("message", "Correo de verificación enviado"));
    }

    @PostMapping("/forgot-password/email/{email}")
    public ResponseEntity<?> forgotPassword(@RequestBody EmailRequest request) {
        notificationService.sendForgotPassword(request.getEmail(), request.getUserId(), request.getLink());
        return ResponseEntity.ok(Collections.singletonMap("message", "Correo de recuperar contrasena enviado"));
    }

    @PostMapping("/finalizada/email/{email}")
    public ResponseEntity<?> notificarPagoRealizado(@RequestBody EmailFinalizadaRequest request) {
        notificationService.sendPagoRealizado(request.getEmail(), request.getUserId(), request.getSubastaId(), request.getTitle());
        return ResponseEntity.ok(Collections.singletonMap("message", "Correo de pago realizado enviado"));
    }

    @PutMapping("/{id}/ocultar")
    public ResponseEntity<Void> ocultarNotificacion(@PathVariable Long id) {
        notificationService.ocultarNotificacion(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/email/welcome/{email}")
    public ResponseEntity<?> notificarBienvenida(@PathVariable String email, @RequestParam Long userId) {
        notificationService.sendWelcomeEmail(email, userId);
        return ResponseEntity.ok(Collections.singletonMap("message", "Correo de bienvenida enviado"));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<NotificationDTO>> getNotificationsUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.consultarNotificacionesUserId(userId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<NotificationDTO>> getNotifications() {
        return ResponseEntity.ok(notificationService.consultarNotificaciones());
    }
}
