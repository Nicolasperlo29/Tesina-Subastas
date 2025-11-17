package org.example.subastas.controller;

import org.example.subastas.DTO.SubastaDTO;
import org.example.subastas.DTO.SubastaDTOEdit;
import org.example.subastas.DTO.SubastaDTOPost;
import org.example.subastas.entity.SubastaEntity;
import org.example.subastas.service.SubastaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subastas")
public class SubastaController {

    @Autowired
    private SubastaService subastaService;

    @PostMapping("/postSubasta")
    public SubastaDTO crearSubasta(@RequestBody SubastaDTOPost subastaDTO) {
        return subastaService.crearSubasta(subastaDTO);
    }

    @GetMapping("/getSubasta/{idUsuario}")
    public List<SubastaDTO> obtenerSubastasIdUsuario(@PathVariable Long idUsuario) {
        return subastaService.obtenerSubastaByIdUsuario(idUsuario);
    }

    @GetMapping("/subasta/userId/{idUsuarioGanador}")
    public List<SubastaDTO> obtenerSubastasIdUsuarioGanador(@PathVariable Long idUsuarioGanador) {
        return subastaService.obtenerSubastaByIdUsuarioGanador(idUsuarioGanador);
    }

    @GetMapping("/activas")
    public List<SubastaDTO> obtenerSubastasActivas() {
        return subastaService.obtenerSubastasActivas();
    }

    @GetMapping("/getAllSubastas")
    public List<SubastaDTO> obtenerSubastas() {
        return subastaService.obtenerSubastas();
    }

    @GetMapping("/subasta/{id}")
    public SubastaDTO obtenerSubastaById(@PathVariable Long id) {
        return subastaService.obtenerSubastaById(id);
    }

    @GetMapping("/estado/{estado}")
    public List<SubastaDTO> obtenerSubastasPorEstado(@PathVariable String estado) {
        return subastaService.obtenerSubastasByEstado(estado.toUpperCase());
    }

    @GetMapping("/getSubastas/{estado}/{categoria}")
    public List<SubastaDTO> obtenerSubastasPorCategoria(@PathVariable String categoria, @PathVariable String estado) {
        return subastaService.obtenerSubastasPorCategoria(categoria, estado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizar(@PathVariable Long id, @RequestBody SubastaDTOEdit dto) {
        subastaService.actualizarSubasta(id, dto);
        return ResponseEntity.ok("Subasta actualizada");
    }

    @PutMapping("/ocultar/{id}")
    public ResponseEntity<String> eliminarSubasta(@PathVariable Long id) {
        subastaService.eliminarSubasta(id);
        return ResponseEntity.ok("Subasta dada de baja correctamente.");
    }

    @PutMapping("/{subastaId}/ganador/{ganadorId}")
    public ResponseEntity<String> actualizarGanador(
            @PathVariable Long subastaId,
            @PathVariable Long ganadorId) {
        try {
            boolean actualizado = subastaService.actualizarGanador(subastaId, ganadorId);
            if (actualizado) {
                return ResponseEntity.ok("Ganador actualizado correctamente.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se pudo actualizar el ganador.");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar el ganador.");
        }
    }

    @GetMapping("/getSubastas/martillero/{martilleroId}")
    public List<SubastaDTO> obtenerSubastasMartilleroId(@PathVariable Long martilleroId) {
        return subastaService.obtenerSubastasPorMartilleroId(martilleroId);
    }

    @GetMapping("/pong")
    public String pong() {
        return "pong";
    }
}
