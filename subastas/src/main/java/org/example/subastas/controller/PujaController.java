package org.example.subastas.controller;

import org.example.subastas.DTO.PujaAutomaticaDTOPost;
import org.example.subastas.DTO.PujaDTO;
import org.example.subastas.DTO.PujaDTOPost;
import org.example.subastas.DTO.SubastaDTO;
import org.example.subastas.entity.PujaEntity;
import org.example.subastas.entity.SubastaEntity;
import org.example.subastas.service.PujaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pujas")
@CrossOrigin(origins = "http://localhost:4200")
public class PujaController {

    @Autowired
    private PujaService pujaService;

    @PostMapping("/postPuja")
    public PujaDTO crearPuja(@RequestBody PujaDTOPost pujaDTO) {
        return pujaService.crearPuja(pujaDTO);
    }

    @GetMapping("/getPujas")
    public List<PujaDTO> obtenerPujas() {
        return pujaService.obtenerPujas();
    }

    @GetMapping("/puja/{subastaId}")
    public PujaDTO obtenerPujas(@PathVariable Long subastaId) {
        return pujaService.obtenerPujaMasAlta(subastaId);
    }

    @GetMapping("/getPujas/subastaId/{subastaId}")
    public List<PujaDTO> obtenerPujasSubastaId(@PathVariable Long subastaId) {
        return pujaService.obtenerPujasSubastaId(subastaId);
    }

    @GetMapping("/pujasUserId/{userId}")
    public List<PujaDTO> obtenerPujasIdUsuario(@PathVariable Long userId) {
        return pujaService.obtenerPujasIdUsuario(userId);
    }

    @PostMapping("/puja-automatica")
    public ResponseEntity<?> activarPujaAutomatica(@RequestBody PujaAutomaticaDTOPost dto) {
        pujaService.crearPujaAutomatica(dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deletePuja/{id}")
    public ResponseEntity<?> eliminarPuja(@PathVariable Long id) {
        boolean eliminada = pujaService.eliminarPuja(id);
        if (eliminada) {
            return ResponseEntity.ok(Map.of("message", "Puja eliminada"));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
