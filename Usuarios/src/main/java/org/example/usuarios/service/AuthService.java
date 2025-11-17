package org.example.usuarios.service;

import org.example.usuarios.DTOS.UserDTOPost;
import org.example.usuarios.entity.UserEntity;

public interface AuthService {

    String login(String email, String password);

    UserEntity registerUser(UserDTOPost user);

    UserEntity activarCuenta(String email);

    void requestPasswordReset(String email);

    void resetPassword(String token, String newPassword);
}
