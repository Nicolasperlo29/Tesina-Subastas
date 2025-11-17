package org.example.notifications.repository;

import org.example.notifications.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Optional<List<Notification>> findAllByUserIdAndMostrarTrue(Long userId);

    Optional<Notification> findFirstByDestinatarioAndUserIdAndSubastaIdAndTipoAndEnviadoTrue(
            String destinatario, Long userId, Long subastaId, String tipo
    );
}
