package org.example.usuarios.DTOS;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTOEdit {

//    private Long id;

    private String name;

    private String lastname;

    private String username;

//    private String email;

//    private String rol;

    private String numberphone;
}
