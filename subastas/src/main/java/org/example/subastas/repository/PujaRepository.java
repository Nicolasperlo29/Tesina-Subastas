package org.example.subastas.repository;

import org.example.subastas.DTO.ReportePujaDTO;
import org.example.subastas.entity.PujaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PujaRepository extends JpaRepository<PujaEntity, Long> {

    PujaEntity findTopBySubasta_IdAndEstadoOrderByValorDesc(Long subastaId, String estado);

    List<PujaEntity> findByEstado(String estado);

    boolean existsBySubastaId(Long subastaId);

    @Query("SELECT MAX(p.valor) FROM PujaEntity p WHERE p.subasta.id = :subastaId")
    BigDecimal findMontoGanadorBySubastaId(Long subastaId);

    @Query("""
    SELECT new org.example.subastas.DTO.ReportePujaDTO(
        p.userId,
        COUNT(p),
        SUM(p.valor),
        AVG(p.valor)
    )
    FROM PujaEntity p
    GROUP BY p.userId
    """)
    List<ReportePujaDTO> obtenerReportePorUsuario();
}
