package org.example.usuarios.service.implementacion;

import org.example.usuarios.DTOS.UserDTOEdit;
import org.example.usuarios.entity.UserEntity;
import org.example.usuarios.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private UserEntity mockUser;
    private UserDTOEdit mockUserDTOEdit;

    @BeforeEach
    void setUp() {
        mockUser = new UserEntity();
        mockUser.setId(1L);
        mockUser.setName("Juan");
        mockUser.setLastname("Perez");
        mockUser.setUsername("juanperez");
        mockUser.setEmail("juan@example.com");
        mockUser.setNumberphone("123456789");
        mockUser.setActivo(true);
        mockUser.setVerified(true);

        mockUserDTOEdit = new UserDTOEdit();
        mockUserDTOEdit.setName("Juan Carlos");
        mockUserDTOEdit.setLastname("Perez Lopez");
        mockUserDTOEdit.setUsername("juancarlos");
        mockUserDTOEdit.setNumberphone("987654321");
    }

    @Test
    void getUsers_CasoExitoso_RetornaListaUsuariosActivos() {
        // Arrange
        UserEntity user1 = new UserEntity();
        user1.setId(1L);
        user1.setName("Juan");
        user1.setActivo(true);

        UserEntity user2 = new UserEntity();
        user2.setId(2L);
        user2.setName("Maria");
        user2.setActivo(true);

        List<UserEntity> expectedUsers = Arrays.asList(user1, user2);
        when(userRepository.findByActivoTrue()).thenReturn(expectedUsers);

        // Act
        List<UserEntity> result = userService.getUsers();

        // Assert
        assertEquals(2, result.size());
        assertEquals(expectedUsers, result);
        verify(userRepository).findByActivoTrue();
    }

    @Test
    void getUserByEmail_CasoExitoso_RetornaUsuario() {
        // Arrange
        String email = "juan@example.com";
        when(userRepository.findByEmailAndActivoTrue(email)).thenReturn(Optional.of(mockUser));

        // Act
        UserEntity result = userService.getUserByEmail(email);

        // Assert
        assertNotNull(result);
        assertEquals(mockUser.getEmail(), result.getEmail());
        assertEquals(mockUser.getName(), result.getName());
        verify(userRepository).findByEmailAndActivoTrue(email);
    }

    @Test
    void getUserById_CasoExitoso_RetornaUsuario() {
        // Arrange
        Long id = 1L;
        when(userRepository.findByIdAndActivoTrue(id)).thenReturn(Optional.of(mockUser));

        // Act
        UserEntity result = userService.getUserById(id);

        // Assert
        assertNotNull(result);
        assertEquals(mockUser.getId(), result.getId());
        assertEquals(mockUser.getName(), result.getName());
        verify(userRepository).findByIdAndActivoTrue(id);
    }

    @Test
    void editarUsuario_CasoExitoso_ActualizaYRetornaDatos() {
        // Arrange
        Long id = 1L;
        UserEntity usuarioActualizado = new UserEntity();
        usuarioActualizado.setId(id);
        usuarioActualizado.setName(mockUserDTOEdit.getName());
        usuarioActualizado.setLastname(mockUserDTOEdit.getLastname());
        usuarioActualizado.setUsername(mockUserDTOEdit.getUsername());
        usuarioActualizado.setNumberphone(mockUserDTOEdit.getNumberphone());

        when(userRepository.findById(id)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(UserEntity.class))).thenReturn(usuarioActualizado);

        // Act
        UserDTOEdit result = userService.editarUsuario(id, mockUserDTOEdit);

        // Assert
        assertNotNull(result);
        assertEquals(mockUserDTOEdit.getName(), result.getName());
        assertEquals(mockUserDTOEdit.getLastname(), result.getLastname());
        assertEquals(mockUserDTOEdit.getUsername(), result.getUsername());
        assertEquals(mockUserDTOEdit.getNumberphone(), result.getNumberphone());

        verify(userRepository).findById(id);
        verify(userRepository).save(argThat(user ->
                user.getName().equals(mockUserDTOEdit.getName()) &&
                        user.getLastname().equals(mockUserDTOEdit.getLastname()) &&
                        user.getUsername().equals(mockUserDTOEdit.getUsername()) &&
                        user.getNumberphone().equals(mockUserDTOEdit.getNumberphone())
        ));
    }

    @Test
    void darDeBaja_CasoExitoso_DesactivaUsuarioRetornaTrue() {
        // Arrange
        String email = "juan@example.com";
        when(userRepository.findByEmailAndActivoTrue(email)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(UserEntity.class))).thenReturn(mockUser);

        // Act
        boolean result = userService.darDeBaja(email);

        // Assert
        assertTrue(result);
        verify(userRepository).findByEmailAndActivoTrue(email);
        verify(userRepository).save(argThat(user ->
                !user.isActivo() && !user.isVerified()
        ));
    }
}