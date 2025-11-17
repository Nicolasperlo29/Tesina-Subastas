package org.example.usuarios.service;

import org.example.usuarios.DTOS.UserDTO;
import org.example.usuarios.DTOS.UserDTOEdit;
import org.example.usuarios.DTOS.UserDTOPost;
import org.example.usuarios.domain.User;
import org.example.usuarios.entity.UserEntity;

import java.util.List;

public interface UserService {

    List<UserEntity> getUsers();

    UserEntity getUserByEmail(String email);

    UserEntity getUserById(Long id);

    UserDTOEdit editarUsuario(Long id, UserDTOEdit userDTOPost);

    boolean darDeBaja(String email);
}
