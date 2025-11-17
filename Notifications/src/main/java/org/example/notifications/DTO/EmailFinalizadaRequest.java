package org.example.notifications.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailFinalizadaRequest {

    private String email;
    private Long userId;
    private Long subastaId;
    private String title;
}
