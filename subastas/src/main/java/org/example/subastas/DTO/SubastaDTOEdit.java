package org.example.subastas.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.subastas.entity.ImagenSubasta;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubastaDTOEdit {

    private String title;

    private BigDecimal monto;

    private String description;

    private List<String> nuevasImagenes;

    private String ubicacion;
}
