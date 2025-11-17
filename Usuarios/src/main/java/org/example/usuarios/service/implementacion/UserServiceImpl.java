package org.example.usuarios.service.implementacion;

import org.example.usuarios.DTOS.*;
import org.example.usuarios.entity.UserEntity;
import org.example.usuarios.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.example.usuarios.repository.UserRepository;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserEntity> getUsers() {    
        List<UserEntity> users = userRepository.findByActivoTrue();

        return users;
    }

    @Override
    public UserEntity getUserByEmail(String email) {
        Optional<UserEntity> user = userRepository.findByEmailAndActivoTrue(email);

        return user.orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));
    }

    @Override
    public UserEntity getUserById(Long id) {
        Optional<UserEntity> user = userRepository.findByIdAndActivoTrue(id);

        return user.orElseThrow(() -> new RuntimeException("Usuario no encontrado con Id: " + id));
    }

    @Override
    public UserDTOEdit editarUsuario(Long id, UserDTOEdit userDTOPost) {
        UserEntity usuario = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        if (userRepository.existsByUsername(userDTOPost.getUsername()) &&
                !usuario.getUsername().equals(userDTOPost.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El username ya existe");
        }

        usuario.setName(userDTOPost.getName());
        usuario.setLastname(userDTOPost.getLastname());
        usuario.setUsername(userDTOPost.getUsername());
        usuario.setNumberphone(userDTOPost.getNumberphone());

        UserEntity actualizado = userRepository.save(usuario);

        return new UserDTOEdit(
                actualizado.getName(),
                actualizado.getLastname(),
                actualizado.getUsername(),
                actualizado.getNumberphone()
        );
    }

    @Override
    public boolean darDeBaja(String email) {
        Optional<UserEntity> optionalUsuario = userRepository.findByEmailAndActivoTrue(email);

        if (optionalUsuario.isPresent()) {
            UserEntity usuario = optionalUsuario.get();
            usuario.setActivo(false);
            usuario.setVerified(false);
            userRepository.save(usuario);
            return true;
        }

        return false;
    }
}
