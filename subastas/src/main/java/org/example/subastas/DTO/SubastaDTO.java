package org.example.subastas.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubastaDTO {

    private Long id;

    private String title;

    private String description;

    private String categoria;

    private BigDecimal precioInicial;

    private Long martilleroId;

    private String ubicacion;

    private Long userGanadorId;

    private BigDecimal incrementoFijo;

    private String emailCreador;

//
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
//    private LocalDateTime fechaInicio;
//
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
//    private LocalDateTime fechaFin;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant fechaInicio;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant fechaFin;

    private List<String> imagenes;

//    private SubastaEstado estado;

    private String estado;

    private Long userId;
}
