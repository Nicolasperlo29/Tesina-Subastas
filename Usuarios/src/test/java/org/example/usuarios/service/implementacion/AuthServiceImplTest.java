package org.example.usuarios.service.implementacion;

import org.example.usuarios.DTOS.NotificationException;
import org.example.usuarios.DTOS.UserDTOPost;
import org.example.usuarios.DTOS.UsuarioYaExistenteException;
import org.example.usuarios.client.NotificationClient;
import org.example.usuarios.config.JwtUtil;
import org.example.usuarios.entity.UserEntity;
import org.example.usuarios.repository.UserRepository;
import org.example.usuarios.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {


    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private NotificationClient notificationClient;

    @Spy
    @InjectMocks
    private AuthServiceImpl authService; // Asume que esta es tu clase de servicio

    private UserEntity mockUser;
    private UserDTOPost mockUserDTO;

    @BeforeEach
    void setUp() {
        mockUser = new UserEntity();
        mockUser.setId(1L);
        mockUser.setEmail("test@example.com");
        mockUser.setPassword("encodedPassword");
        mockUser.setVerified(true);
        mockUser.setActivo(true);

        mockUserDTO = new UserDTOPost();
        mockUserDTO.setEmail("newuser@example.com");
        mockUserDTO.setPassword("plainPassword");
        mockUserDTO.setName("Test");
        mockUserDTO.setLastname("User");
//        mockUserDTO.setUsername("testuser");
//        mockUserDTO.setNumberphone("123456789");
        mockUserDTO.setRol("USER");
        mockUserDTO.setCaptchaResponse("valid-captcha");
    }


    @Test
    void login_CasoExitoso_RetornaToken() {
        // Arrange
        String email = "test@example.com";
        String password = "plainPassword";
        String expectedToken = "jwt-token";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(password, mockUser.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(email)).thenReturn(expectedToken);

        // Act
        String result = authService.login(email, password);

        // Assert
        assertEquals(expectedToken, result);
        verify(userRepository).findByEmail(email);
        verify(passwordEncoder).matches(password, mockUser.getPassword());
        verify(jwtUtil).generateToken(email);
    }


    @Test
    void registerUser_CasoExitoso_CreaUsuarioYEnviaEmail() throws Exception {
        // Arrange
        UserEntity savedUser = new UserEntity();
        savedUser.setId(1L);
        savedUser.setEmail(mockUserDTO.getEmail());

        when(userRepository.findByEmail(mockUserDTO.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(mockUserDTO.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedUser);
        doNothing().when(notificationClient).notifyVerificationEmail(anyString(), anyLong(), anyString());

        // Mockear el método verifyRecaptcha para que siempre retorne true
        doReturn(true).when(authService).verifyRecaptcha(anyString());

        // Act
        UserEntity result = authService.registerUser(mockUserDTO);

        // Assert
        assertNotNull(result);
        assertEquals(mockUserDTO.getEmail(), result.getEmail());

        verify(userRepository).findByEmail(mockUserDTO.getEmail());
        verify(passwordEncoder).encode(mockUserDTO.getPassword());
        verify(userRepository).save(argThat(user ->
                user.getEmail().equals(mockUserDTO.getEmail()) &&
                        user.getName().equals(mockUserDTO.getName()) &&
                        user.isActivo() &&
                        !user.isVerified() &&
                        user.getVerificationToken() != null
        ));
        verify(notificationClient).notifyVerificationEmail(eq(savedUser.getEmail()), eq(savedUser.getId()), contains("verify?token="));
    }

    @Test
    void activarCuenta_CasoExitoso_ActivaUsuarioYEnviaEmail() {
        // Arrange
        UserEntity inactiveUser = new UserEntity();
        inactiveUser.setId(2L);
        inactiveUser.setEmail("inactive@example.com");
        inactiveUser.setActivo(false);

        when(userRepository.findByEmail("inactive@example.com")).thenReturn(Optional.of(inactiveUser));
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(notificationClient).notifyVerificationEmail(anyString(), anyLong(), anyString());

        // Act
        UserEntity result = authService.activarCuenta("inactive@example.com");

        // Assert
        assertNotNull(result);
        assertEquals("inactive@example.com", result.getEmail());
        assertNotNull(result.getVerificationToken());

        verify(userRepository).findByEmail("inactive@example.com");
        verify(userRepository).save(argThat(user -> user.getVerificationToken() != null));
        verify(notificationClient).notifyVerificationEmail(eq(inactiveUser.getEmail()), eq(inactiveUser.getId()), contains("verify?token="));
    }

    @Test
    void activarCuenta_CuandoUsuarioNoExiste_LanzaRuntimeException() {
        // Arrange
        when(userRepository.findByEmail("noexiste@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.activarCuenta("noexiste@example.com");
        });

        assertEquals("No existe el usuario", exception.getMessage());
        verify(userRepository).findByEmail("noexiste@example.com");
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(notificationClient);
    }

    @Test
    void activarCuenta_CuandoUsuarioYaActivo_LanzaRuntimeException() {
        // Arrange
        UserEntity activeUser = new UserEntity();
        activeUser.setId(3L);
        activeUser.setEmail("activo@example.com");
        activeUser.setActivo(true);

        when(userRepository.findByEmail("activo@example.com")).thenReturn(Optional.of(activeUser));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.activarCuenta("activo@example.com");
        });

        assertEquals("El usuario ya esta activo", exception.getMessage());
        verify(userRepository).findByEmail("activo@example.com");
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(notificationClient);
    }

}