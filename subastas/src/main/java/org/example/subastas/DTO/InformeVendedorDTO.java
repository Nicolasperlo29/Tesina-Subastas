package org.example.subastas.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InformeVendedorDTO {

    private Long idVendedor;
    private Long subastasFinalizadas;
    private Long subastasVendidas;
    private BigDecimal totalVendido;
    private BigDecimal promedioVenta;
}
