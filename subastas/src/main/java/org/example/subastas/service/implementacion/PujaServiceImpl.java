package org.example.subastas.service.implementacion;

import org.example.subastas.DTO.PujaAutomaticaDTOPost;
import org.example.subastas.DTO.PujaDTO;
import org.example.subastas.DTO.PujaDTOPost;
import org.example.subastas.domain.Puja;
import org.example.subastas.domain.PujaEvent;
import org.example.subastas.entity.PujaAutomaticaEntity;
import org.example.subastas.entity.PujaEntity;
import org.example.subastas.entity.SubastaEntity;
import org.example.subastas.repository.PujaAutomaticaRepository;
import org.example.subastas.repository.PujaRepository;
import org.example.subastas.repository.SubastaRepository;
import org.example.subastas.service.PujaService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PujaServiceImpl implements PujaService {

    @Autowired
    private PujaRepository pujaRepository;

    @Autowired
    private SubastaRepository subastaRepository;

    @Autowired
    private PujaAutomaticaRepository pujaAutomaticaRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ApplicationEventPublisher eventPublisher;


    @Override
    public PujaDTO crearPuja(PujaDTOPost pujaDTO) {

        SubastaEntity subasta = subastaRepository.findById(pujaDTO.getSubastaId())
                .orElseThrow(() -> new IllegalArgumentException("Subasta no encontrada"));

        PujaEntity pujaEntity = new PujaEntity();
        pujaEntity.setValor(pujaDTO.getValor());
        pujaEntity.setSubasta(subasta);
        pujaEntity.setUserId(pujaDTO.getUserId());
        pujaEntity.setEstado("aceptada");
        pujaEntity.setFechaCreada(Instant.now());

        PujaEntity pujaAnterior = pujaRepository.findTopBySubasta_IdAndEstadoOrderByValorDesc(subasta.getId(), "aceptada");

        if (pujaAnterior == null) {
            pujaAnterior = new PujaEntity();
            pujaAnterior.setValor(new BigDecimal(0));
        }

        if (pujaAnterior.getValor().compareTo(pujaEntity.getValor()) >= 0) {
            throw new IllegalArgumentException("La puja debe ser mayor a la anterior");
        }

        Instant horaPuja = pujaEntity.getFechaCreada();
        Duration tiempoRestante = Duration.between(horaPuja, subasta.getFechaFin());

        if (!tiempoRestante.isNegative() && tiempoRestante.toMinutes() <= 3) {
            subasta.setFechaFin(subasta.getFechaFin().plus(Duration.ofMinutes(3)));
            subastaRepository.save(subasta);
        }

        pujaRepository.save(pujaEntity);

        PujaDTO puja = new PujaDTO();
        puja.setValor(pujaEntity.getValor());
        puja.setSubastaId(pujaEntity.getSubasta().getId());
        puja.setId(pujaEntity.getId());
        puja.setUserId(pujaEntity.getUserId());
        puja.setEstado(pujaEntity.getEstado());
        puja.setFechaCreada(pujaEntity.getFechaCreada());

        eventPublisher.publishEvent(new PujaEvent(this, puja.getSubastaId(), puja.getValor()));

        return puja;
    }

    @Override
    public List<PujaDTO> obtenerPujas() {
        List<PujaEntity> listPujas = pujaRepository.findAll();
        List<PujaDTO> listPujasDTO = new ArrayList<>();

        for (PujaEntity p : listPujas) {
           listPujasDTO.add( modelMapper.map(p, PujaDTO.class));
        }

        return listPujasDTO;
    }

    @Override
    public PujaDTO obtenerPujaMasAlta(Long subastaId) {
        PujaEntity pujaEntity = pujaRepository.findTopBySubasta_IdAndEstadoOrderByValorDesc(subastaId, "aceptada");

        if (pujaEntity == null) {
            return null;
        }

        PujaDTO puja = new PujaDTO();
        puja.setValor(pujaEntity.getValor());
        puja.setSubastaId(pujaEntity.getSubasta().getId());
        puja.setId(pujaEntity.getId());
        puja.setUserId(pujaEntity.getUserId());
        puja.setEstado(pujaEntity.getEstado());
        puja.setFechaCreada(pujaEntity.getFechaCreada());

        return puja;
    }

    @Override
    public List<PujaDTO> obtenerPujasIdUsuario(Long userId) {
        List<PujaEntity> pujas = pujaRepository.findByEstado("aceptada");
        List<PujaDTO> pujasDTO = new ArrayList<>();

        for (PujaEntity p : pujas) {
            if (p.getUserId().equals(userId)) {
                pujasDTO.add(modelMapper.map(p, PujaDTO.class));
            }
        }

        return pujasDTO;
    }

    @Override
    public List<PujaDTO> obtenerPujasSubastaId(Long subastaId) {
        List<PujaEntity> pujas = pujaRepository.findAll();
        List<PujaDTO> pujasDTO = new ArrayList<>();

        for (PujaEntity p : pujas) {
            if (p.getSubasta().getId().equals(subastaId) && p.getEstado().equals("aceptada")) {
                pujasDTO.add(modelMapper.map(p, PujaDTO.class));
            }
        }

        return pujasDTO;
    }

    @Override
    public boolean eliminarPuja(Long pujaId) {
        Optional<PujaEntity> puja = pujaRepository.findById(pujaId);
        if (puja.isPresent()) {
            puja.get().setEstado("rechazada");
            pujaRepository.save(puja.get());
            return true;
        }
        return false;
    }

    @Override
    public PujaDTO crearPujaAutomatica(PujaAutomaticaDTOPost pujaDTO) {
        SubastaEntity subasta = subastaRepository.findById(pujaDTO.getSubastaId())
                .orElseThrow(() -> new RuntimeException("Subasta no encontrada"));

        PujaAutomaticaEntity pujaAuto = new PujaAutomaticaEntity();
        pujaAuto.setUserId(pujaDTO.getUserId());
        pujaAuto.setSubasta(subasta);
        pujaAuto.setValorMaximo(pujaDTO.getValorMaximo());
        pujaAuto.setEstado("aceptada");
        pujaAuto.setFechaCreada(Instant.now());

        pujaAutomaticaRepository.save(pujaAuto);

        PujaDTO puja = new PujaDTO();
        puja.setValor(pujaAuto.getValorMaximo());
        puja.setSubastaId(pujaAuto.getSubasta().getId());
        puja.setId(pujaAuto.getId());
        puja.setUserId(pujaAuto.getUserId());
        puja.setEstado(pujaAuto.getEstado());
        puja.setFechaCreada(pujaAuto.getFechaCreada());

        return puja;
    }
}
