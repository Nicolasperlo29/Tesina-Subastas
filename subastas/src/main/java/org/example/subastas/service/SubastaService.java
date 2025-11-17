package org.example.subastas.service;

import org.example.subastas.DTO.SubastaDTO;
import org.example.subastas.DTO.SubastaDTOEdit;
import org.example.subastas.DTO.SubastaDTOPost;
import org.example.subastas.entity.SubastaEntity;

import java.util.List;

public interface SubastaService {

    SubastaDTO crearSubasta(SubastaDTOPost subastaDTO);

    List<SubastaDTO> obtenerSubastaByIdUsuario(Long idUsuario);

    List<SubastaDTO> obtenerSubastaByIdUsuarioGanador(Long idUsuario);

    SubastaDTO obtenerSubastaById(Long id);

    List<SubastaDTO> obtenerSubastasActivas();

    List<SubastaDTO> obtenerSubastasPendientes();

    List<SubastaDTO> obtenerSubastas();

    boolean eliminarSubasta(Long subastaId);

    List<SubastaDTO> obtenerSubastasByEstado(String estado);

    void actualizarEstadosSubastas();

    List<SubastaDTO> obtenerSubastasPorCategoria(String categoria, String estado);

    List<SubastaDTO> obtenerSubastasPorMartilleroId(Long martilleroId);

    boolean actualizarGanador(Long subastaId, Long ganadorId);

    SubastaEntity actualizarSubasta(Long id, SubastaDTOEdit subastaDTOEdit);
}
