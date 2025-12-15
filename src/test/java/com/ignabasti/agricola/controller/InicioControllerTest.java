package com.ignabasti.agricola.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InicioControllerTest {

    @Test
    void inicio_debeRetornarVistaInicio() {
        InicioController controller = new InicioController();

        String view = controller.inicio();

        assertEquals("inicio", view);
    }
}