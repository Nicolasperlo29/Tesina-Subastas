package org.example.subastas.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PujaAutomaticaDTOPost {

    private BigDecimal valorMaximo;

    private Long subastaId;

    private Long userId;
}
