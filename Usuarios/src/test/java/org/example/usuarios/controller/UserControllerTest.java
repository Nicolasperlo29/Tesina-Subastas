package org.example.usuarios.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.usuarios.DTOS.UserDTOEdit;
import org.example.usuarios.entity.UserEntity;
import org.example.usuarios.service.implementacion.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private UserController userController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void getUsersTest() throws Exception {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setEmail("test@example.com");

        when(userService.getUsers()).thenReturn(List.of(user));

        mockMvc.perform(get("/user/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("test@example.com"));
    }

    @Test
    void getUserByIdTest() throws Exception {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setEmail("user1@example.com");

        when(userService.getUserById(1L)).thenReturn(user);

        mockMvc.perform(get("/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user1@example.com"));
    }

    @Test
    void editarUsuarioTest() throws Exception {
        UserDTOEdit dto = new UserDTOEdit();
        dto.setName("NuevoNombre");

        when(userService.editarUsuario(1L, dto)).thenReturn(dto);

        mockMvc.perform(put("/user/editar/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("NuevoNombre"));
    }

    @Test
    void darDeBajaTest() throws Exception {
        when(userService.darDeBaja("baja@example.com")).thenReturn(true);

        mockMvc.perform(get("/user/darDeBaja")
                        .param("email", "baja@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}