package org.example.pagos.repositories;

import org.example.pagos.entities.DepositoGarantiaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepositoRepository extends JpaRepository<DepositoGarantiaEntity, Long> {

    boolean existsByUserIdAndSubastaIdAndActivoTrue(Long usuarioId, Long subastaId);

    Optional<DepositoGarantiaEntity> findByUserIdAndSubastaIdAndActivoTrue(Long userId, Long subastaId);
}
