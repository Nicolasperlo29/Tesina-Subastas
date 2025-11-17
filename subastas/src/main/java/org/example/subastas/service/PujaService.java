package org.example.subastas.service;

import org.example.subastas.DTO.PujaAutomaticaDTOPost;
import org.example.subastas.DTO.PujaDTO;
import org.example.subastas.DTO.PujaDTOPost;
import org.example.subastas.entity.PujaEntity;

import java.util.List;

public interface PujaService {

    PujaDTO crearPuja(PujaDTOPost pujaDTO);

    List<PujaDTO> obtenerPujas();

    PujaDTO obtenerPujaMasAlta(Long subastaId);

    List<PujaDTO> obtenerPujasIdUsuario(Long userId);

    List<PujaDTO> obtenerPujasSubastaId(Long subastaId);

    boolean eliminarPuja(Long pujaId);

    PujaDTO crearPujaAutomatica(PujaAutomaticaDTOPost pujaDTO);
}
