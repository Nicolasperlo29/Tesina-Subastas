package org.example.pagos.DTOS;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepositoGarantiaDTO {

    private BigDecimal monto;

    private Long userId;

    private Long subastaId;
}
