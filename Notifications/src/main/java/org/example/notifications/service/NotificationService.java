package org.example.notifications.service;

import org.example.notifications.DTO.NotificationDTO;

import java.util.List;

public interface NotificationService {

    void sendVerificationEmail(String to, Long userId, String link);

    void sendWelcomeEmail(String to, Long userId);

    void sendGanadorEmail(String to, Long userId, Long subastaId);

    void sendPagoRealizado(String to, Long userId, Long subastaId, String title);

    void sendSubastaCreada(String to, Long userId, Long subastaId);

    void sendForgotPassword(String to, Long userId, String link);

    List<NotificationDTO> consultarNotificacionesUserId(Long userId);

    void ocultarNotificacion(Long id);

    List<NotificationDTO> consultarNotificaciones();
}
