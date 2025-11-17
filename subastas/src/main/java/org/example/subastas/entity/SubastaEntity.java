package org.example.subastas.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.example.subastas.DTO.SubastaEstado;
import org.example.subastas.domain.Categoria;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "subastas")
public class SubastaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant fechaInicio;

//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant fechaFin;

    @OneToMany(mappedBy = "subasta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImagenSubasta> imagenes = new ArrayList<>();

//    private SubastaEstado estado;

    private String estado;

    private Long userId;
}
