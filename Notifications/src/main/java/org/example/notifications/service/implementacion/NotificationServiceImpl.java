package org.example.notifications.service.implementacion;

import org.example.notifications.DTO.NotificationDTO;
import org.example.notifications.entity.Notification;
import org.example.notifications.repository.NotificationRepository;
import org.example.notifications.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    public void sendVerificationEmail(String to, Long userId, String link) {
        String tipo = "VERIFICATION";
        Long subastaId = null;
        String subject = "Verificá tu cuenta";
        String body = "Hacé clic en el siguiente enlace para verificar tu cuenta: " + link;
        sendEmail(to, userId, subject, body, subastaId, tipo);
    }

    @Override
    public void sendWelcomeEmail(String to, Long userId) {
        String tipo = "BIENVENIDA";
        Long subastaId = null;
        String subject = "¡Bienvenido!";
        String body = "Gracias por registrarte. ¡Nos alegra tenerte!";
        sendEmail(to, userId, subject, body, subastaId, tipo);
    }

    @Override
    public void sendGanadorEmail(String to, Long userId, Long subastaId) {
        String tipo = "GANADOR_SUBASTA";

        Optional<Notification> yaEnviada = notificationRepository
                .findFirstByDestinatarioAndUserIdAndSubastaIdAndTipoAndEnviadoTrue(to, userId, subastaId, tipo);

        if (yaEnviada.isPresent()) {
            System.out.println("Ya se envió la notificación de ganador al usuario " + userId + " para la subasta " + subastaId);
            return;
        }

        String subject = "¡Ganaste la subasta!";
        String body = "Por favor, dirigite hacia la misma en la web para abonar.";
        sendEmail(to, userId, subject, body, subastaId, tipo);
    }

    @Override
    public void sendPagoRealizado(String to, Long userId, Long subastaId, String title) {
        String tipo = "PAGO_REALIZADO";

        Optional<Notification> yaEnviada = notificationRepository
                .findFirstByDestinatarioAndUserIdAndSubastaIdAndTipoAndEnviadoTrue(to, userId, subastaId, tipo);

        if (yaEnviada.isPresent()) {
            System.out.println("Ya se envió la notificación de pago " + userId + " para la subasta " + subastaId);
            return;
        }

        String subject = "Pago acreditado";
        String body = "¡Ya realizaron el pago de tu subasta: " + title  + ". Nos pondremos en contacto para coordinar la entrega de tu producto!";
        sendEmail(to, userId, subject, body, subastaId, tipo);
    }

    @Override
    public void sendSubastaCreada(String to, Long userId, Long subastaId) {
        String tipo = "SUBASTA_CREADA";

        Optional<Notification> yaEnviada = notificationRepository
                .findFirstByDestinatarioAndUserIdAndSubastaIdAndTipoAndEnviadoTrue(to, userId, subastaId, tipo);

        if (yaEnviada.isPresent()) {
            System.out.println("Ya se envió la notificación de ganador al usuario " + userId + " para la subasta " + subastaId);
            return;
        }

        String subject = "Creaste una subasta";
        String body = "Tu subasta fue creada exitosamente.";
        sendEmail(to, userId, subject, body, subastaId, tipo);
    }

    @Override
    public void sendForgotPassword(String to, Long userId, String link) {
        String tipo = "FORGOT_PASSWORD";
        Long subastaId = null;
        String subject = "Recuperar contrasena";
        String body = "Hacé clic en el siguiente enlace para recuperar tu contrasena: " + link;
        sendEmail(to, userId, subject, body, subastaId, tipo);
    }

    @Override
    public List<NotificationDTO> consultarNotificacionesUserId(Long userId) {
        Optional<List<Notification>> notifications = notificationRepository.findAllByUserIdAndMostrarTrue(userId);
        List<NotificationDTO> notificationDTOS = new ArrayList<>();

        if (notifications.isEmpty()) {
            throw new RuntimeException("No hay notificaciones para el usuario con id: " + userId);
        }
        for (Notification n : notifications.get()) {
            notificationDTOS.add(new NotificationDTO(n.getId(), n.getDestinatario(), n.getUserId(), n.getAsunto(), n.getCuerpo(), n.getFechaEnvio()));
        }

        return notificationDTOS;
    }

    @Override
    public void ocultarNotificacion(Long id) {
        Optional<Notification> notification = notificationRepository.findById(id);

        if (notification.isEmpty()) {
            throw new RuntimeException("No hay notificacion para ocultar.");
        }

        if (!notification.get().isMostrar()) {
            throw new RuntimeException("La notificacion ya se oculto anteriormente.");
        }

        notification.get().setMostrar(false);

        notificationRepository.save(notification.get());
    }

    @Override
    public List<NotificationDTO> consultarNotificaciones() {
        List<Notification> notifications = notificationRepository.findAll();
        List<NotificationDTO> notificationDTOS = new ArrayList<>();

        if (notifications.isEmpty()) {
            throw new RuntimeException("No hay notificaciones.");
        }
        for (Notification n : notifications) {
            notificationDTOS.add(new NotificationDTO(n.getId(), n.getDestinatario(), n.getUserId(), n.getAsunto(), n.getCuerpo(), n.getFechaEnvio()));
        }

        return notificationDTOS;
    }

    private void sendEmail(String to, Long userId, String subject, String body, Long subastaId, String tipo) {
        Notification notificacion = new Notification();
        notificacion.setDestinatario(to);
        notificacion.setAsunto(subject);
        notificacion.setCuerpo(body);
        notificacion.setFechaEnvio(LocalDateTime.now());
        notificacion.setUserId(userId);
        notificacion.setSubastaId(subastaId);
        notificacion.setTipo(tipo);
        notificacion.setMostrar(true);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);

            notificacion.setEnviado(true);
        } catch (Exception e) {
            notificacion.setEnviado(false);
            notificacion.setError(e.getMessage());
            System.err.println("Error al enviar email a " + to + ": " + e.getMessage());
        }

        notificationRepository.save(notificacion);
    }
}
