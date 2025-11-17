package org.example.pagos.DTOS;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagoWebhookDTO {

    private String mpPaymentId;
    private String estado;
    private BigDecimal monto;
    private LocalDateTime fechaCreacion;
    private String metodoPago;
    private String tipoPago;
    private String emailUsuario;
    private Long usuarioId;
    private Long subastaId;
}
