package org.example.usuarios.controller;

import org.example.usuarios.DTOS.*;
import org.example.usuarios.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.example.usuarios.service.UserService;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

//    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public List<UserEntity> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserEntity> getUserById(@PathVariable Long id) {
        UserEntity entity = userService.getUserById(id);
        return ResponseEntity.ok(entity);
    }

    @PutMapping("/editar/{id}")
    public ResponseEntity<UserDTOEdit> editarUsuario(@PathVariable Long id, @RequestBody UserDTOEdit userDTOPost) {
        UserDTOEdit usuarioActualizado = userService.editarUsuario(id, userDTOPost);
        return ResponseEntity.ok(usuarioActualizado);
    }

    @GetMapping("/me")
    public ResponseEntity<UserEntity> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        UserEntity entity = userService.getUserByEmail(email);
        return ResponseEntity.ok(entity);
    }

    @GetMapping("/darDeBaja")
    public ResponseEntity<Boolean> darDeBaja(@RequestParam String email) {
        boolean resultado = userService.darDeBaja(email);
        return ResponseEntity.ok(resultado);
    }
}
