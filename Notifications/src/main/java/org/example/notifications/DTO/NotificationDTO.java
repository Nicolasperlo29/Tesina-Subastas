package org.example.notifications.DTO;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {

    private Long id;

    private String destinatario;

    private Long userId;

    private String asunto;

    private String cuerpo;

    private LocalDateTime fechaEnvio;
}
