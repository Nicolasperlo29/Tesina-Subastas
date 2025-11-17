package org.example.pagos.DTOS;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubastaDTO {

    private Long id;

    private String title;

    private String emailCreador;

    private Long userGanadorId;
}
