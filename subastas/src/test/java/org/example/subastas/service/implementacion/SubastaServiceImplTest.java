package org.example.subastas.service.implementacion;

import org.example.subastas.DTO.SubastaDTO;
import org.example.subastas.DTO.SubastaDTOEdit;
import org.example.subastas.DTO.SubastaDTOPost;
import org.example.subastas.client.UsuariosApi;
import org.example.subastas.entity.ImagenSubasta;
import org.example.subastas.entity.SubastaEntity;
import org.example.subastas.repository.PujaRepository;
import org.example.subastas.repository.SubastaRepository;
import org.example.subastas.service.implementacion.SubastaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubastaServiceImplTest {

    @Mock
    private SubastaRepository subastaRepository;

    @Mock
    private UsuariosApi usuariosApi;

    @Mock
    private PujaRepository pujaRepository;

    @InjectMocks
    private SubastaServiceImpl subastaService;

    private SubastaDTOPost subastaDTOPost;
    private SubastaEntity subastaEntity;
    private Instant fechaInicio;
    private Instant fechaFin;

    @BeforeEach
    void setUp() {
        fechaInicio = Instant.now().plus(1, ChronoUnit.HOURS);
        fechaFin = Instant.now().plus(2, ChronoUnit.HOURS);

        subastaDTOPost = new SubastaDTOPost();
        subastaDTOPost.setTitle("Test Subasta");
        subastaDTOPost.setDescription("Descripción de prueba");
        subastaDTOPost.setCategoria("Electronica");
        subastaDTOPost.setPrecioInicial(new BigDecimal("100.00"));
        subastaDTOPost.setFechaInicio(fechaInicio);
        subastaDTOPost.setFechaFin(fechaFin);
        subastaDTOPost.setUserId(1L);
        subastaDTOPost.setMartilleroId(1L);
        subastaDTOPost.setUbicacion("Buenos Aires");
        subastaDTOPost.setIncrementoFijo(new BigDecimal("10.00"));
        subastaDTOPost.setEmailCreador("test@example.com");
        subastaDTOPost.setImagenes(Arrays.asList("imagen1.jpg", "imagen2.jpg"));

        subastaEntity = new SubastaEntity();
        subastaEntity.setId(1L);
        subastaEntity.setTitle("Test Subasta");
        subastaEntity.setDescription("Descripción de prueba");
        subastaEntity.setCategoria("Electronica");
        subastaEntity.setPrecioInicial(new BigDecimal("100.00"));
        subastaEntity.setFechaInicio(fechaInicio);
        subastaEntity.setFechaFin(fechaFin);
        subastaEntity.setUserId(1L);
        subastaEntity.setMartilleroId(1L);
        subastaEntity.setUbicacion("Buenos Aires");
        subastaEntity.setIncrementoFijo(new BigDecimal("10.00"));
        subastaEntity.setEmailCreador("test@example.com");
        subastaEntity.setEstado("PENDIENTE");

        List<ImagenSubasta> imagenes = new ArrayList<>();
        ImagenSubasta imagen1 = new ImagenSubasta();
        imagen1.setUrl("imagen1.jpg");
        imagen1.setSubasta(subastaEntity);
        imagenes.add(imagen1);
        subastaEntity.setImagenes(imagenes);
    }

    @Test
    void testCrearSubasta() {
        // Given
        when(usuariosApi.existeUsuario(1L)).thenReturn(true);
        when(subastaRepository.save(any(SubastaEntity.class))).thenReturn(subastaEntity);

        // When
        SubastaDTO resultado = subastaService.crearSubasta(subastaDTOPost);

        // Then
        assertNotNull(resultado);
        assertEquals("Test Subasta", resultado.getTitle());
        assertEquals("PENDIENTE", resultado.getEstado());
        verify(usuariosApi).existeUsuario(1L);
        verify(subastaRepository).save(any(SubastaEntity.class));
    }

    @Test
    void testObtenerSubastaByIdUsuario() {
        // Given
        List<SubastaEntity> subastas = Arrays.asList(subastaEntity);
        when(subastaRepository.findAll()).thenReturn(subastas);

        // When
        List<SubastaDTO> resultado = subastaService.obtenerSubastaByIdUsuario(1L);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Test Subasta", resultado.get(0).getTitle());
        verify(subastaRepository).findAll();
    }

    @Test
    void testObtenerSubastaByIdUsuarioGanador() {
        // Given
        subastaEntity.setUserGanadorId(1L);
        List<SubastaEntity> subastas = Arrays.asList(subastaEntity);
        when(subastaRepository.findAll()).thenReturn(subastas);

        // When
        List<SubastaDTO> resultado = subastaService.obtenerSubastaByIdUsuarioGanador(1L);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getUserGanadorId());
        verify(subastaRepository).findAll();
    }

    @Test
    void testObtenerSubastaById() {
        // Given
        when(subastaRepository.findById(1L)).thenReturn(Optional.of(subastaEntity));

        // When
        SubastaDTO resultado = subastaService.obtenerSubastaById(1L);

        // Then
        assertNotNull(resultado);
        assertEquals("Test Subasta", resultado.getTitle());
        assertEquals(1L, resultado.getId());
        verify(subastaRepository).findById(1L);
    }

    @Test
    void testActualizarEstadosSubastas() {
        // Given
        SubastaEntity subastaVencida = new SubastaEntity();
        subastaVencida.setId(1L);
        subastaVencida.setEstado("ACTIVA");
        subastaVencida.setFechaInicio(Instant.now().minus(2, ChronoUnit.HOURS));
        subastaVencida.setFechaFin(Instant.now().minus(1, ChronoUnit.HOURS));

        List<SubastaEntity> subastas = Arrays.asList(subastaVencida);
        when(subastaRepository.findAll()).thenReturn(subastas);

        // When
        subastaService.actualizarEstadosSubastas();

        // Then
        verify(subastaRepository).findAll();
        verify(subastaRepository).saveAll(anyList());
        assertEquals("FINALIZADA", subastaVencida.getEstado());
    }

    @Test
    void testObtenerSubastasActivas() {
        // Given
        List<SubastaEntity> subastasActivas = Arrays.asList(subastaEntity);
        when(subastaRepository.findByEstado("ACTIVA")).thenReturn(subastasActivas);

        // When
        List<SubastaDTO> resultado = subastaService.obtenerSubastasActivas();

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(subastaRepository).findByEstado("ACTIVA");
    }

    @Test
    void testObtenerSubastasPendientes() {
        // Given
        List<SubastaEntity> subastasPendientes = Arrays.asList(subastaEntity);
        when(subastaRepository.findByEstado("PENDIENTE")).thenReturn(subastasPendientes);

        // When
        List<SubastaDTO> resultado = subastaService.obtenerSubastasPendientes();

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(subastaRepository).findByEstado("PENDIENTE");
    }

    @Test
    void testObtenerSubastasByEstado() {
        // Given
        List<SubastaEntity> subastas = Arrays.asList(subastaEntity);
        when(subastaRepository.findByEstado("ACTIVA")).thenReturn(subastas);

        // When
        List<SubastaDTO> resultado = subastaService.obtenerSubastasByEstado("ACTIVA");

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(subastaRepository).findByEstado("ACTIVA");
    }

    @Test
    void testObtenerSubastas() {
        // Given
        List<SubastaEntity> subastas = Arrays.asList(subastaEntity);
        when(subastaRepository.findAllByOrderByFechaInicioDesc()).thenReturn(subastas);

        // When
        List<SubastaDTO> resultado = subastaService.obtenerSubastas();

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(subastaRepository).findAllByOrderByFechaInicioDesc();
    }

    @Test
    void testObtenerSubastasPorCategoria() {
        // Given
        subastaEntity.setEstado("ACTIVA");
        List<SubastaEntity> subastas = Arrays.asList(subastaEntity);
        when(subastaRepository.findByEstado("ACTIVA")).thenReturn(subastas);

        // When
        List<SubastaDTO> resultado = subastaService.obtenerSubastasPorCategoria("Electronica", "ACTIVA");

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Electronica", resultado.get(0).getCategoria());
        verify(subastaRepository).findByEstado("ACTIVA");
    }

    @Test
    void testObtenerSubastasPorMartilleroId() {
        // Given
        subastaEntity.setEstado("ACTIVA");
        List<SubastaEntity> subastas = Arrays.asList(subastaEntity);
        when(subastaRepository.findByEstado("ACTIVA")).thenReturn(subastas);

        // When
        List<SubastaDTO> resultado = subastaService.obtenerSubastasPorMartilleroId(1L);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getMartilleroId());
        verify(subastaRepository).findByEstado("ACTIVA");
    }

    @Test
    void testActualizarGanador() {
        // Given
        when(subastaRepository.findById(1L)).thenReturn(Optional.of(subastaEntity));
        when(subastaRepository.save(any(SubastaEntity.class))).thenReturn(subastaEntity);

        // When
        boolean resultado = subastaService.actualizarGanador(1L, 2L);

        // Then
        assertTrue(resultado);
        verify(subastaRepository).findById(1L);
        verify(subastaRepository).save(any(SubastaEntity.class));
        assertEquals(2L, subastaEntity.getUserGanadorId());
        assertEquals("FINALIZADA", subastaEntity.getEstado());
    }

    @Test
    void testActualizarSubasta() {
        // Given
        SubastaDTOEdit subastaDTOEdit = new SubastaDTOEdit();
        subastaDTOEdit.setTitle("Título Actualizado");
        subastaDTOEdit.setDescription("Descripción Actualizada");
        subastaDTOEdit.setMonto(new BigDecimal("150.00"));
        subastaDTOEdit.setUbicacion("Córdoba");
        subastaDTOEdit.setNuevasImagenes(Arrays.asList("nueva_imagen.jpg"));

        when(subastaRepository.findById(1L)).thenReturn(Optional.of(subastaEntity));
        when(pujaRepository.existsBySubastaId(1L)).thenReturn(false);
        when(subastaRepository.save(any(SubastaEntity.class))).thenReturn(subastaEntity);

        // When
        SubastaEntity resultado = subastaService.actualizarSubasta(1L, subastaDTOEdit);

        // Then
        assertNotNull(resultado);
        verify(subastaRepository).findById(1L);
        verify(pujaRepository).existsBySubastaId(1L);
        verify(subastaRepository).save(any(SubastaEntity.class));
    }

}
