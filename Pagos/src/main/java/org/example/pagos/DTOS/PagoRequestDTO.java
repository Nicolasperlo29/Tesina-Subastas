package org.example.pagos.DTOS;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagoRequestDTO {

    private Long subastaId;
    private Long usuarioId;
    private String titulo;
    private BigDecimal precioFinal;
}
