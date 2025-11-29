package com.ignabasti.agricola.controller;

import com.ignabasti.agricola.dto.UsuarioDTO;
import com.ignabasti.agricola.service.UsuarioService;
import com.ignabasti.agricola.service.AuthenticationHelper;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

class PerfilControllerTest {
    private MockMvc mockMvc;
    private UsuarioDTO usuario;
    @Mock
    private UsuarioService usuarioService;
    @Mock
    private AuthenticationHelper authenticationHelper;
    @Mock
    private Model model;
    @InjectMocks
    private PerfilController perfilController;

    @BeforeEach
    void setUp() { 
        MockitoAnnotations.openMocks(this); 

        mockMvc = MockMvcBuilders.standaloneSetup(perfilController)
            .setViewResolvers(new InternalResourceViewResolver("/WEB-INF/views/", ".jsp"))
            .build();

        usuario = new UsuarioDTO();
        usuario.setCorreo("usuario@mail.com");
        usuario.setDireccion("Mi direccion");
        usuario.setTelefono("123456789");
        usuario.setCultivos("Trigo, Maíz");
    }

    @Test
    void mostrarPerfil_debeAgregarUsuarioAlModeloYRetornarVista() {
        UsuarioDTO usuario = new UsuarioDTO();
        usuario.setNombre("Pedro");
        when(authenticationHelper.getAuthenticatedUserEmail()).thenReturn("pedro@mail.com");
        when(usuarioService.obtenerUsuarioPorCorreo("pedro@mail.com")).thenReturn(usuario);
        String view = perfilController.mostrarPerfil(model);
        verify(model).addAttribute("usuario", usuario);
        assertEquals("perfil", view);
    }

    // MostrarPerfil
    @Test
    void mostrarPerfil_exitoso() throws Exception {
        when(authenticationHelper.getAuthenticatedUserEmail()).thenReturn("usuario@mail.com");
        when(usuarioService.obtenerUsuarioPorCorreo("usuario@mail.com")).thenReturn(usuario);

        mockMvc.perform(get("/perfil"))
            .andExpect(status().isOk())
            .andExpect(view().name("perfil"))
            .andExpect(model().attributeExists("usuario"));
    }

    @Test
    void mostrarPerfil_error_catch() throws Exception {
        when(authenticationHelper.getAuthenticatedUserEmail()).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/perfil"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login"));
    }

    // ActualizarPerfil
    @Test
    void actualizarPerfil_exitoso() throws Exception {
        when(authenticationHelper.getAuthenticatedUserEmail()).thenReturn("usuario@mail.com");
        when(usuarioService.obtenerUsuarioPorCorreo("usuario@mail.com")).thenReturn(usuario);

        mockMvc.perform(post("/perfil")
                        .param("direccion", "Nueva direccion")
                        .param("telefono", "987654321")
                        .param("cultivos", "Soja")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("perfil"))
                .andExpect(model().attributeExists("exito"))
                .andExpect(model().attributeExists("usuario"));

        verify(usuarioService).actualizarUsuario(eq("usuario@mail.com"), any(UsuarioDTO.class));
    }

    @Test
    void actualizarPerfil_error_catch() throws Exception {
        when(authenticationHelper.getAuthenticatedUserEmail()).thenReturn("usuario@mail.com");
        when(usuarioService.obtenerUsuarioPorCorreo("usuario@mail.com"))
                .thenThrow(new RuntimeException("fallo"));

        mockMvc.perform(post("/perfil")
                        .param("direccion", "Nueva direccion")
                        .param("telefono", "987654321")
                        .param("cultivos", "Soja")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("perfil"))
                .andExpect(model().attributeExists("error"));
    }
}
