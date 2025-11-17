package org.example.subastas.service.implementacion;

import org.example.subastas.DTO.PujaAutomaticaDTOPost;
import org.example.subastas.DTO.PujaDTO;
import org.example.subastas.DTO.PujaDTOPost;
import org.example.subastas.domain.PujaEvent;
import org.example.subastas.entity.PujaAutomaticaEntity;
import org.example.subastas.entity.PujaEntity;
import org.example.subastas.entity.SubastaEntity;
import org.example.subastas.repository.PujaAutomaticaRepository;
import org.example.subastas.repository.PujaRepository;
import org.example.subastas.repository.SubastaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PujaServiceImplTest {


    @Mock
    private PujaRepository pujaRepository;

    @Mock
    private SubastaRepository subastaRepository;

    @Mock
    private PujaAutomaticaRepository pujaAutomaticaRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private PujaServiceImpl pujaService;

    private SubastaEntity subastaEntity;
    private PujaEntity pujaEntity;
    private PujaDTOPost pujaDTOPost;
    private PujaDTO pujaDTO;

    @BeforeEach
    void setUp() {
        // Configurar SubastaEntity
        subastaEntity = new SubastaEntity();
        subastaEntity.setId(1L);
        subastaEntity.setTitle("Test Subasta");
        subastaEntity.setFechaFin(Instant.now().plus(1, ChronoUnit.HOURS));

        // Configurar PujaEntity
        pujaEntity = new PujaEntity();
        pujaEntity.setId(1L);
        pujaEntity.setValor(new BigDecimal("150.00"));
        pujaEntity.setSubasta(subastaEntity);
        pujaEntity.setUserId(1L);
        pujaEntity.setEstado("aceptada");
        pujaEntity.setFechaCreada(Instant.now());

        // Configurar PujaDTOPost
        pujaDTOPost = new PujaDTOPost();
        pujaDTOPost.setValor(new BigDecimal("200.00"));
        pujaDTOPost.setSubastaId(1L);
        pujaDTOPost.setUserId(2L);

        // Configurar PujaDTO
        pujaDTO = new PujaDTO();
        pujaDTO.setId(1L);
        pujaDTO.setValor(new BigDecimal("150.00"));
        pujaDTO.setSubastaId(1L);
        pujaDTO.setUserId(1L);
        pujaDTO.setEstado("aceptada");
        pujaDTO.setFechaCreada(Instant.now());
    }

    @Test
    void testCrearPuja() {
        // Given
        PujaEntity pujaAnterior = new PujaEntity();
        pujaAnterior.setValor(new BigDecimal("100.00"));

        when(subastaRepository.findById(1L)).thenReturn(Optional.of(subastaEntity));
        when(pujaRepository.findTopBySubasta_IdAndEstadoOrderByValorDesc(1L, "aceptada"))
                .thenReturn(pujaAnterior);
        when(pujaRepository.save(any(PujaEntity.class))).thenReturn(pujaEntity);

        // When
        PujaDTO resultado = pujaService.crearPuja(pujaDTOPost);

        // Then
        assertNotNull(resultado);
        assertEquals(new BigDecimal("200.00"), resultado.getValor());
        assertEquals("aceptada", resultado.getEstado());
        assertEquals(2L, resultado.getUserId());
        verify(subastaRepository).findById(1L);
        verify(pujaRepository).save(any(PujaEntity.class));
        verify(eventPublisher).publishEvent(any(PujaEvent.class));
    }

    @Test
    void testObtenerPujas() {
        // Given
        List<PujaEntity> pujas = Arrays.asList(pujaEntity);
        when(pujaRepository.findAll()).thenReturn(pujas);
        when(modelMapper.map(pujaEntity, PujaDTO.class)).thenReturn(pujaDTO);

        // When
        List<PujaDTO> resultado = pujaService.obtenerPujas();

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(pujaDTO, resultado.get(0));
        verify(pujaRepository).findAll();
        verify(modelMapper).map(pujaEntity, PujaDTO.class);
    }

    @Test
    void testObtenerPujaMasAlta() {
        // Given
        when(pujaRepository.findTopBySubasta_IdAndEstadoOrderByValorDesc(1L, "aceptada"))
                .thenReturn(pujaEntity);

        // When
        PujaDTO resultado = pujaService.obtenerPujaMasAlta(1L);

        // Then
        assertNotNull(resultado);
        assertEquals(new BigDecimal("150.00"), resultado.getValor());
        assertEquals(1L, resultado.getUserId());
        assertEquals("aceptada", resultado.getEstado());
        verify(pujaRepository).findTopBySubasta_IdAndEstadoOrderByValorDesc(1L, "aceptada");
    }

    @Test
    void testObtenerPujasIdUsuario() {
        // Given
        List<PujaEntity> pujas = Arrays.asList(pujaEntity);
        when(pujaRepository.findByEstado("aceptada")).thenReturn(pujas);
        when(modelMapper.map(pujaEntity, PujaDTO.class)).thenReturn(pujaDTO);

        // When
        List<PujaDTO> resultado = pujaService.obtenerPujasIdUsuario(1L);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(pujaDTO, resultado.get(0));
        verify(pujaRepository).findByEstado("aceptada");
        verify(modelMapper).map(pujaEntity, PujaDTO.class);
    }

    @Test
    void testObtenerPujasSubastaId() {
        // Given
        List<PujaEntity> pujas = Arrays.asList(pujaEntity);
        when(pujaRepository.findAll()).thenReturn(pujas);
        when(modelMapper.map(pujaEntity, PujaDTO.class)).thenReturn(pujaDTO);

        // When
        List<PujaDTO> resultado = pujaService.obtenerPujasSubastaId(1L);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(pujaDTO, resultado.get(0));
        verify(pujaRepository).findAll();
        verify(modelMapper).map(pujaEntity, PujaDTO.class);
    }

    @Test
    void testEliminarPuja() {
        // Given
        when(pujaRepository.findById(1L)).thenReturn(Optional.of(pujaEntity));
        when(pujaRepository.save(any(PujaEntity.class))).thenReturn(pujaEntity);

        // When
        boolean resultado = pujaService.eliminarPuja(1L);

        // Then
        assertTrue(resultado);
        assertEquals("rechazada", pujaEntity.getEstado());
        verify(pujaRepository).findById(1L);
        verify(pujaRepository).save(pujaEntity);
    }

    @Test
    void testCrearPujaAutomatica() {
        // Given
        PujaAutomaticaDTOPost pujaAutomaticaDTO = new PujaAutomaticaDTOPost();
        pujaAutomaticaDTO.setSubastaId(1L);
        pujaAutomaticaDTO.setUserId(1L);
        pujaAutomaticaDTO.setValorMaximo(new BigDecimal("300.00"));

        PujaAutomaticaEntity pujaAutomatica = new PujaAutomaticaEntity();
        pujaAutomatica.setId(1L);
        pujaAutomatica.setUserId(1L);
        pujaAutomatica.setSubasta(subastaEntity);
        pujaAutomatica.setValorMaximo(new BigDecimal("300.00"));
        pujaAutomatica.setEstado("aceptada");
        pujaAutomatica.setFechaCreada(Instant.now());

        when(subastaRepository.findById(1L)).thenReturn(Optional.of(subastaEntity));
        when(pujaAutomaticaRepository.save(any(PujaAutomaticaEntity.class))).thenReturn(pujaAutomatica);

        // When
        PujaDTO resultado = pujaService.crearPujaAutomatica(pujaAutomaticaDTO);

        // Then
        assertNotNull(resultado);
        assertEquals(new BigDecimal("300.00"), resultado.getValor());
        assertEquals(1L, resultado.getUserId());
        assertEquals("aceptada", resultado.getEstado());
        verify(subastaRepository).findById(1L);
        verify(pujaAutomaticaRepository).save(any(PujaAutomaticaEntity.class));
    }
}