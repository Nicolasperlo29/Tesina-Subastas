package org.example.usuarios.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.usuarios.DTOS.LoginRequest;
import org.example.usuarios.DTOS.UserDTOPost;
import org.example.usuarios.client.NotificationClient;
import org.example.usuarios.entity.UserEntity;
import org.example.usuarios.repository.UserRepository;
import org.example.usuarios.service.AuthService;
import org.example.usuarios.service.implementacion.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockitoBean
//    private AuthServiceImpl authService;
//
//    @MockitoBean
//    private UserRepository userRepository;
//
//    @MockitoBean
//    private NotificationClient notificationClient;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private LoginRequest loginRequest;
//    private UserDTOPost userDTOPost;
//    private UserEntity mockUser;
//
//    @BeforeEach
//    void setUp() {
//        loginRequest = new LoginRequest();
//        loginRequest.setEmail("test@example.com");
//        loginRequest.setPassword("password123");
//
//        userDTOPost = new UserDTOPost();
//        userDTOPost.setEmail("newuser@example.com");
//        userDTOPost.setPassword("password123");
//        userDTOPost.setName("Test");
//        userDTOPost.setLastname("User");
//        userDTOPost.setUsername("testuser");
//        userDTOPost.setNumberphone("123456789");
//        userDTOPost.setRol("USER");
//        userDTOPost.setCaptchaResponse("valid-captcha");
//
//        mockUser = new UserEntity();
//        mockUser.setId(1L);
//        mockUser.setEmail("test@example.com");
//        mockUser.setName("Test");
//        mockUser.setLastname("User");
//        mockUser.setVerified(false);
//        mockUser.setActivo(false);
//        mockUser.setVerificationToken("test-token-123");
//    }
//
//    @Test
//    void login_CasoExitoso_RetornaTokenJWT() throws Exception {
//        // Arrange
//        String expectedToken = "jwt-token-123";
//        when(authService.login(loginRequest.getEmail(), loginRequest.getPassword()))
//                .thenReturn(expectedToken);
//
//        // Act & Assert
//        mockMvc.perform(post("/auth/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(loginRequest)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.token").value(expectedToken));
//
//        verify(authService).login(loginRequest.getEmail(), loginRequest.getPassword());
//    }
//
//    @Test
//    void register_CasoExitoso_RetornaUsuarioCreado() throws Exception {
//        // Arrange
//        UserEntity savedUser = new UserEntity();
//        savedUser.setId(1L);
//        savedUser.setEmail(userDTOPost.getEmail());
//        savedUser.setName(userDTOPost.getName());
//
//        when(authService.registerUser(any(UserDTOPost.class))).thenReturn(savedUser);
//
//        // Act & Assert
//        mockMvc.perform(post("/auth/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(userDTOPost)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1L))
//                .andExpect(jsonPath("$.email").value(userDTOPost.getEmail()))
//                .andExpect(jsonPath("$.name").value(userDTOPost.getName()));
//
//        verify(authService).registerUser(argThat(dto ->
//                dto.getEmail().equals(userDTOPost.getEmail()) &&
//                        dto.getName().equals(userDTOPost.getName())
//        ));
//    }
//
//    @Test
//    void verifyUser_CasoExitoso_VerificaUsuarioYRetornaMensaje() throws Exception {
//        // Arrange
//        String token = "test-token-123";
//        when(userRepository.findByVerificationToken(token)).thenReturn(Optional.of(mockUser));
//        when(userRepository.save(any(UserEntity.class))).thenReturn(mockUser);
//        doNothing().when(notificationClient).notifyBienvenidaEmail(anyString(), anyLong());
//
//        // Act & Assert
//        mockMvc.perform(get("/auth/verify")
//                        .param("token", token))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Usuario verificado correctamente"));
//
//        verify(userRepository).findByVerificationToken(token);
//        verify(userRepository).save(argThat(user ->
//                user.isVerified() &&
//                        user.isActivo() &&
//                        user.getVerificationToken() == null
//        ));
//        verify(notificationClient).notifyBienvenidaEmail(mockUser.getEmail(), mockUser.getId());
//    }
//
//    @Test
//    void activarCuenta_CasoExitoso_RetornaUsuarioActivado() throws Exception {
//        // Arrange
//        String email = "test@example.com";
//        UserEntity userActivado = new UserEntity();
//        userActivado.setId(1L);
//        userActivado.setEmail(email);
//        userActivado.setActivo(true);
//
//        when(authService.activarCuenta(email)).thenReturn(userActivado);
//
//        // Act & Assert
//        mockMvc.perform(post("/auth/activar")
//                        .param("email", email))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1L))
//                .andExpect(jsonPath("$.email").value(email))
//                .andExpect(jsonPath("$.activo").value(true));
//
//        verify(authService).activarCuenta(email);
//    }

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationClient notificationClient;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void login_CasoExitoso_RetornaToken() throws Exception {
        String email = "test@example.com";
        String password = "password";
        String token = "jwt-token";

        when(authService.login(email, password)).thenReturn(token);

        String jsonRequest = """
            {
                "email": "%s",
                "password": "%s"
            }
            """.formatted(email, password);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token));

        verify(authService).login(email, password);
    }

    @Test
    void register_CasoExitoso_RetornaUsuario() throws Exception {
        UserDTOPost userDTO = new UserDTOPost();
        userDTO.setEmail("newuser@example.com");
        userDTO.setPassword("password");
        userDTO.setName("Test");

        UserEntity savedUser = new UserEntity();
        savedUser.setEmail(userDTO.getEmail());
        savedUser.setId(1L);

        when(authService.registerUser(any(UserDTOPost.class))).thenReturn(savedUser);

        String jsonRequest = """
            {
                "email": "%s",
                "password": "%s",
                "name": "%s"
            }
            """.formatted(userDTO.getEmail(), userDTO.getPassword(), userDTO.getName());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(userDTO.getEmail()));

        verify(authService).registerUser(any(UserDTOPost.class));
    }

    @Test
    void verifyUser_TokenValido_VerificaUsuario() throws Exception {
        String token = "valid-token";
        UserEntity user = new UserEntity();
        user.setEmail("user@example.com");
        user.setId(1L);
        user.setVerified(false);
        user.setActivo(false);
        user.setVerificationToken(token);

        when(userRepository.findByVerificationToken(token)).thenReturn(Optional.of(user));
        when(userRepository.save(any(UserEntity.class))).thenReturn(user);
        doNothing().when(notificationClient).notifyBienvenidaEmail(user.getEmail(), user.getId());

        mockMvc.perform(get("/auth/verify")
                        .param("token", token))
                .andExpect(status().isOk())
                .andExpect(content().string("Usuario verificado correctamente"));

        verify(userRepository).findByVerificationToken(token);
        verify(userRepository).save(any(UserEntity.class));
        verify(notificationClient).notifyBienvenidaEmail(user.getEmail(), user.getId());
    }

    @Test
    void verifyUser_TokenInvalido_BadRequest() throws Exception {
        String token = "invalid-token";

        when(userRepository.findByVerificationToken(token)).thenReturn(Optional.empty());

        mockMvc.perform(get("/auth/verify")
                        .param("token", token))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Token inválido"));

        verify(userRepository).findByVerificationToken(token);
    }

    @Test
    void activarCuenta_CasoExitoso_RetornaUsuario() throws Exception {
        String email = "user@example.com";
        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setId(1L);

        when(authService.activarCuenta(email)).thenReturn(user);

        mockMvc.perform(post("/auth/activar")
                        .param("email", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));

        verify(authService).activarCuenta(email);
    }

    @Test
    void activarCuenta_Error_BadRequest() throws Exception {
        String email = "user@example.com";

        when(authService.activarCuenta(email)).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(post("/auth/activar")
                        .param("email", email))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(""));

        verify(authService).activarCuenta(email);
    }

    @Test
    void pong_RetornaHola() throws Exception {
        mockMvc.perform(get("/auth/pong"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hola"));
    }
}