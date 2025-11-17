package org.example.notifications.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String destinatario;

    private Long userId;

    private String asunto;

    @Column(columnDefinition = "TEXT")
    private String cuerpo;

    private Long subastaId;

    private boolean mostrar;

    private String tipo;

    private LocalDateTime fechaEnvio;

    private boolean enviado;

    private String error; // si hubo un error al enviar
}
