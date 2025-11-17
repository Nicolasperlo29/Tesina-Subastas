package org.example.subastas.repository;

import org.example.subastas.entity.PujaAutomaticaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PujaAutomaticaRepository extends JpaRepository<PujaAutomaticaEntity, Long> {

    List<PujaAutomaticaEntity> findAllBySubasta_IdAndEstado(Long subastaId, String estado);
}
