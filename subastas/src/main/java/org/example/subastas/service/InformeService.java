package org.example.subastas.service;

import org.example.subastas.DTO.InformeVendedorDTO;
import org.example.subastas.DTO.ReportePujaDTO;

import java.util.List;

public interface InformeService {

    List<InformeVendedorDTO> generarInforme();

    List<ReportePujaDTO> obtenerReportePujas();
}
