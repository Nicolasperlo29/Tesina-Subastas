package org.example.subastas.controller;

import org.example.subastas.DTO.InformeVendedorDTO;
import org.example.subastas.DTO.ReportePujaDTO;
import org.example.subastas.service.implementacion.InformeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/informe")
public class InformeController {

    @Autowired
    private InformeServiceImpl informeService;

    @GetMapping("/vendedores")
    public List<InformeVendedorDTO> getInformeVendedores() {
        return informeService.generarInforme();
    }

    @GetMapping("/pujas-por-usuario")
    public List<ReportePujaDTO> getReportePujasPorUsuario() {
        return informeService.obtenerReportePujas();
    }
}
