package org.example.subastas.repository;

import org.example.subastas.entity.SubastaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubastaRepository extends JpaRepository<SubastaEntity, Long> {

    List<SubastaEntity> findAllByOrderByFechaInicioDesc();

    List<SubastaEntity> findByEstado(String estado);
}
