package org.example.subastas.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Subasta {

    private Long id;

    private String title;

    private String description;

    private Categoria categoria;

    private BigDecimal precioInicial;

    private LocalDateTime fechaInicio;

    private LocalDateTime fechaFin;

    private Long userId;
}
