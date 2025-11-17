package org.example.subastas.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PujaDTOPost {

    private BigDecimal valor;

    private Long subastaId;

    private Long userId;

    private String estado;
}
