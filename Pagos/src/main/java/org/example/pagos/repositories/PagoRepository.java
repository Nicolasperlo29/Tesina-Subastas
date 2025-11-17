package org.example.pagos.repositories;

import org.example.pagos.entities.PagoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<PagoEntity, Long> {

    Optional<PagoEntity> findByUsuarioIdAndSubastaIdAndEstado(Long usuarioId, Long subastaId, String estado);

    Optional<PagoEntity> findTopBySubastaIdOrderByFechaConfirmacionDesc(Long subastaId);

    boolean existsByUsuarioIdAndSubastaIdAndEstado(Long usuarioId, Long subastaId, String estado);

    List<PagoEntity> findByUsuarioId(Long usuarioId);
}
