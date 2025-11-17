package org.example.subastas.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportePujaDTO {

    private Long userId;
    private Long cantidadPujas;
    private BigDecimal totalOfertado;
    private double promedioOfertado;
}
