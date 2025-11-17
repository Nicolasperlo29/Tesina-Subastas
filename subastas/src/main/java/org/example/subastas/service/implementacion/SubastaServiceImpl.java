package org.example.subastas.service.implementacion;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.example.subastas.DTO.*;
import org.example.subastas.client.UsuariosApi;
import org.example.subastas.entity.ImagenSubasta;
import org.example.subastas.entity.SubastaEntity;
import org.example.subastas.repository.PujaRepository;
import org.example.subastas.repository.SubastaRepository;
import org.example.subastas.service.SubastaService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SubastaServiceImpl implements SubastaService {

    @Autowired
    private SubastaRepository subastaRepository;

    @Autowired
    private UsuariosApi usuariosApi;

    @Autowired
    private PujaRepository pujaRepository;

    @Override
    public SubastaDTO crearSubasta(SubastaDTOPost subastaDTO) {


        if (!usuariosApi.existeUsuario(subastaDTO.getUserId())) {
            throw new IllegalArgumentException("El usuario con ID " + subastaDTO.getUserId() + " no existe.");
        }

        Instant ahora = Instant.now();

        // Validaciones de fechas
        if (subastaDTO.getFechaInicio().plusSeconds(600).isBefore(ahora)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser anterior al momento actual.");
        }

        if (subastaDTO.getFechaInicio().isAfter(subastaDTO.getFechaFin())) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin.");
        }

        SubastaEntity subastaEntity = new SubastaEntity();
        subastaEntity.setTitle(subastaDTO.getTitle());
        subastaEntity.setDescription(subastaDTO.getDescription());
        subastaEntity.setCategoria(subastaDTO.getCategoria());
        subastaEntity.setPrecioInicial(subastaDTO.getPrecioInicial());
        subastaEntity.setFechaInicio(subastaDTO.getFechaInicio());
        subastaEntity.setFechaFin(subastaDTO.getFechaFin());
        subastaEntity.setUserId(subastaDTO.getUserId());
        subastaEntity.setMartilleroId(subastaDTO.getMartilleroId());
        subastaEntity.setUbicacion(subastaDTO.getUbicacion());
        subastaEntity.setUserGanadorId(null);
        subastaEntity.setIncrementoFijo(subastaDTO.getIncrementoFijo());
        subastaEntity.setEmailCreador(subastaDTO.getEmailCreador());

        if (subastaDTO.getFechaInicio().isAfter(ahora)) {
            subastaEntity.setEstado("PENDIENTE");
        } else {
            subastaEntity.setEstado("ACTIVA");
        }

        List<ImagenSubasta> imagenes = new ArrayList<>();
        for (String url : subastaDTO.getImagenes()) {
            ImagenSubasta imagen = new ImagenSubasta();
            imagen.setUrl(url);
            imagen.setSubasta(subastaEntity);
            imagenes.add(imagen);
        }
        subastaEntity.setImagenes(imagenes);

        subastaRepository.save(subastaEntity);

        return convertirASubastaDTO(subastaEntity);
    }

    @Override
    public List<SubastaDTO> obtenerSubastaByIdUsuario(Long idUsuario) {
        List<SubastaDTO> subastasUsuario = subastaRepository.findAll().stream()
                .filter(s -> s.getUserId().equals(idUsuario))
                .map(this::convertirASubastaDTO)
                .collect(Collectors.toList());

        if (subastasUsuario.isEmpty()) {
            throw new IllegalArgumentException("No hay subastas para mostrar");
        }

        return subastasUsuario;
    }

    @Override
    public List<SubastaDTO> obtenerSubastaByIdUsuarioGanador(Long idUsuario) {
        List<SubastaDTO> subastasUsuario = subastaRepository.findAll().stream()
                .filter(s -> s.getUserGanadorId() != null && s.getUserGanadorId().equals(idUsuario))
                .map(this::convertirASubastaDTO)
                .collect(Collectors.toList());

        if (subastasUsuario.isEmpty()) {
            throw new IllegalArgumentException("No hay subastas para mostrar");
        }

        return subastasUsuario;
    }

    @Override
    public SubastaDTO obtenerSubastaById(Long id) {
        return subastaRepository.findById(id)
                .map(this::convertirASubastaDTO)
                .orElseThrow(() -> new IllegalArgumentException("No hay una subasta con ese id"));
    }

    @Transactional
    @Override
    public void actualizarEstadosSubastas() {
        Instant ahora = Instant.now();
        List<SubastaEntity> subastas = subastaRepository.findAll();

        List<SubastaEntity> actualizadas = new ArrayList<>();

        for (SubastaEntity s : subastas) {
            boolean cambiar = false;

            // Si ya terminó, siempre debe estar FINALIZADA
            if (s.getFechaFin() != null && s.getFechaFin().isBefore(ahora)) {
                if (!"FINALIZADA".equals(s.getEstado())) {
                    s.setEstado("FINALIZADA");
                    cambiar = true;
                }
            } else if (s.getFechaInicio() != null && s.getFechaInicio().isBefore(ahora)) {
                // Solo se activa si NO está finalizada y su fecha de inicio ya pasó
                if (!"ACTIVA".equals(s.getEstado())) {
                    s.setEstado("ACTIVA");
                    cambiar = true;
                }
            }

            if (cambiar) {
                actualizadas.add(s);
                System.out.println("Estado actualizado: Subasta " + s.getId() + " -> " + s.getEstado());
            }
        }

        if (!actualizadas.isEmpty()) {
            subastaRepository.saveAll(actualizadas);
        }
    }

    @Override
    public List<SubastaDTO> obtenerSubastasActivas() {
        String estado = "ACTIVA";
        List<SubastaEntity> subastas = subastaRepository.findByEstado(estado);

        return subastas.stream()
                .map(this::convertirASubastaDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SubastaDTO> obtenerSubastasPendientes() {
        String estado = "PENDIENTE";
        List<SubastaEntity> subastas = subastaRepository.findByEstado(estado);

        return subastas.stream()
                .map(this::convertirASubastaDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SubastaDTO> obtenerSubastasByEstado(String estado) {
        return subastaRepository.findByEstado(estado).stream()
                .map(this::convertirASubastaDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SubastaDTO> obtenerSubastas() {
        return subastaRepository.findAllByOrderByFechaInicioDesc().stream()
                .map(this::convertirASubastaDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SubastaDTO> obtenerSubastasPorCategoria(String categoria, String estado) {
        return obtenerSubastasByEstado(estado).stream()
                .filter(subasta -> subasta.getCategoria().equalsIgnoreCase(categoria))
                .collect(Collectors.toList());
    }

    @Override
    public List<SubastaDTO> obtenerSubastasPorMartilleroId(Long martilleroId) {
        return obtenerSubastasActivas().stream()
                .filter(s -> martilleroId.equals(s.getMartilleroId()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean actualizarGanador(Long subastaId, Long ganadorId) {
        SubastaEntity subasta = subastaRepository.findById(subastaId)
                .orElseThrow(() -> new IllegalArgumentException("No existe la subasta"));

        subasta.setUserGanadorId(ganadorId);
        subasta.setEstado("FINALIZADA");
        subastaRepository.save(subasta);
        return true;
    }

    @Override
    public boolean eliminarSubasta(Long subastaId) {
        SubastaEntity subasta = subastaRepository.findById(subastaId)
                .orElseThrow(() -> new IllegalArgumentException("No existe la subasta"));

        boolean pujas = pujaRepository.existsBySubastaId(subastaId);

        if (pujas) {
            throw new RuntimeException("No se pueden eliminar subastas con pujas");
        }

        subasta.setEstado("OCULTA");
        subasta.setFechaFin(Instant.now());
        subastaRepository.save(subasta);
        return true;
    }

    @Override
    public SubastaEntity actualizarSubasta(Long id, SubastaDTOEdit subastaDTOEdit) {
        SubastaEntity subasta = subastaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Subasta no encontrada"));

        boolean tienePujas = pujaRepository.existsBySubastaId(id);
        if (tienePujas && subastaDTOEdit.getMonto() != null &&
                subastaDTOEdit.getMonto().compareTo(subasta.getPrecioInicial()) != 0) {
            throw new IllegalStateException("No se puede modificar el monto: ya existen pujas.");
        }

        subasta.setDescription(subastaDTOEdit.getDescription());
        subasta.setTitle(subastaDTOEdit.getTitle());
        subasta.setPrecioInicial(subastaDTOEdit.getMonto());
        subasta.setUbicacion(subastaDTOEdit.getUbicacion());

        List<ImagenSubasta> imagenes = new ArrayList<>();

        subasta.getImagenes().clear();

        for (String url : subastaDTOEdit.getNuevasImagenes()) {
            ImagenSubasta imagen = new ImagenSubasta();
            imagen.setUrl(url);
            imagen.setSubasta(subasta);
            subasta.getImagenes().add(imagen);
        }

        return subastaRepository.save(subasta);
    }

    private SubastaDTO convertirASubastaDTO(SubastaEntity s) {
        SubastaDTO dto = new SubastaDTO();
        dto.setId(s.getId());
        dto.setTitle(s.getTitle());
        dto.setDescription(s.getDescription());
        dto.setCategoria(s.getCategoria());
        dto.setPrecioInicial(s.getPrecioInicial());
        dto.setMartilleroId(s.getMartilleroId());
        dto.setFechaInicio(s.getFechaInicio());
        dto.setFechaFin(s.getFechaFin());
        dto.setEstado(s.getEstado());
        dto.setUserId(s.getUserId());
        dto.setUbicacion(s.getUbicacion());
        dto.setUserGanadorId(s.getUserGanadorId());
        dto.setIncrementoFijo(s.getIncrementoFijo());
        dto.setEmailCreador(s.getEmailCreador());

        List<String> urls = s.getImagenes()
                .stream()
                .map(ImagenSubasta::getUrl)
                .collect(Collectors.toList());
        dto.setImagenes(urls);
        return dto;
    }
}
