package com.ignabasti.agricola.controller;

import com.ignabasti.agricola.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import static org.junit.jupiter.api.Assertions.*;

class RegistroControllerTest {
    @Mock
    private UsuarioService usuarioService;
    @Mock
    private Model model;
    @InjectMocks
    private RegistroController registroController;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void registro_debeRetornarVista() {
        String view = registroController.registro();
        assertEquals("registro", view);
    }
}
