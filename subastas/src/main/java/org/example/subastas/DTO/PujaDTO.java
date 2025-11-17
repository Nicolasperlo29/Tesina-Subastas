package org.example.subastas.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PujaDTO {

    private Long id;

    private BigDecimal valor;

    private Long subastaId;

    private Long userId;

    private String estado;

    private Instant fechaCreada;
}
