package org.example.usuarios.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private Long id;

    private String name;

    private String lastname;

    private String username;

    private String email;

    private String password;

    private String numberphone;

    private List<String> roles;
}
