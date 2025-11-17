package org.example.usuarios.DTOS;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTOPost {

    private String name;

    private String lastname;

//    private String username;

    private String email;

    private String password;

    private String rol = "user";

//    private String numberphone;

    private boolean acceptTerms;

    private String captchaResponse;
}
