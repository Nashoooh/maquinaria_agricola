package com.ignabasti.agricola.controller;

import com.ignabasti.agricola.dto.UsuarioDTO;
import com.ignabasti.agricola.service.UsuarioService;
import com.ignabasti.agricola.service.AuthenticationHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PerfilControllerTest {
    @Mock
    private UsuarioService usuarioService;
    @Mock
    private AuthenticationHelper authenticationHelper;
    @Mock
    private Model model;
    @InjectMocks
    private PerfilController perfilController;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

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
}
