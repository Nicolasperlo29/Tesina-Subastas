package org.example.subastas.service.implementacion;

import org.example.subastas.DTO.InformeVendedorDTO;
import org.example.subastas.DTO.ReportePujaDTO;
import org.example.subastas.entity.SubastaEntity;
import org.example.subastas.repository.PujaRepository;
import org.example.subastas.repository.SubastaRepository;
import org.example.subastas.service.InformeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InformeServiceImpl implements InformeService {

    @Autowired
    private SubastaRepository subastaRepository;

    @Autowired
    private PujaRepository pujaRepository;

    @Override
    public List<InformeVendedorDTO> generarInforme() {
        List<SubastaEntity> subastasFinalizadas = subastaRepository.findByEstado("FINALIZADA");

        Map<Long, List<SubastaEntity>> subastasPorVendedor = subastasFinalizadas.stream()
                .collect(Collectors.groupingBy(SubastaEntity::getUserId));

        List<InformeVendedorDTO> informe = new ArrayList<>();

        for (Map.Entry<Long, List<SubastaEntity>> entry : subastasPorVendedor.entrySet()) {
            Long vendedorId = entry.getKey();
            List<SubastaEntity> subastas = entry.getValue();

            long totalFinalizadas = subastas.size();
            long totalVendidas = subastas.stream().filter(s -> s.getUserGanadorId() != null).count();

            BigDecimal totalVendido = subastas.stream()
                    .filter(s -> s.getUserGanadorId() != null)
                    .map(s -> {
                        BigDecimal montoGanador = pujaRepository.findMontoGanadorBySubastaId(s.getId());
                        return montoGanador != null ? montoGanador : BigDecimal.ZERO;
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal promedio = totalVendidas > 0
                    ? totalVendido.divide(BigDecimal.valueOf(totalVendidas), 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            informe.add(new InformeVendedorDTO(
                    vendedorId,
                    totalFinalizadas,
                    totalVendidas,
                    totalVendido,
                    promedio
            ));
        }

        return informe;
    }

    public List<ReportePujaDTO> obtenerReportePujas() {
        return pujaRepository.obtenerReportePorUsuario();
    }
}
