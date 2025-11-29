package com.ignabasti.agricola.controller;

import com.ignabasti.agricola.dto.UsuarioDTO;
import com.ignabasti.agricola.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RegistroControllerTest {
    private MockMvc mockMvc;
    @Mock
    private UsuarioService usuarioService;
    @Mock
    private Model model;
    @InjectMocks
    private RegistroController registroController;

    @BeforeEach
    void setUp() { 
        MockitoAnnotations.openMocks(this);
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/"); // o donde estén tus html/thymeleaf
        viewResolver.setSuffix(".html");

        mockMvc = MockMvcBuilders.standaloneSetup(registroController)
            .setViewResolvers(viewResolver)
            .build();
    }

    @Test
    void registro_debeRetornarVista() {
        String view = registroController.registro();
        assertEquals("registro", view);
    }

    // RegistrarUsuarioDesdeRegistro

    @Test
    void registrarUsuario_correoDuplicado() throws Exception {
        when(usuarioService.existePorCorreo("test@mail.com")).thenReturn(true);

        mockMvc.perform(post("/registro")
                .param("nombre", "Test")
                .param("correo", "test@mail.com")
                .param("contrasena", "123456"))
                .andExpect(status().isOk())
                .andExpect(view().name("registro"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "El correo ya está registrado."));

        verify(usuarioService, never()).registrarUsuario(any());
    }

    @Test
    void registrarUsuario_errorExcepcion() throws Exception {
        when(usuarioService.existePorCorreo("error@mail.com")).thenReturn(false);
        doThrow(new RuntimeException("Fallo BD")).when(usuarioService).registrarUsuario(any(UsuarioDTO.class));

        mockMvc.perform(post("/registro")
                .param("nombre", "Error")
                .param("correo", "error@mail.com")
                .param("contrasena", "123456"))
                .andExpect(status().isOk())
                .andExpect(view().name("registro"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "Error al registrar usuario: Fallo BD"));

        verify(usuarioService).registrarUsuario(any(UsuarioDTO.class));
    }
}
