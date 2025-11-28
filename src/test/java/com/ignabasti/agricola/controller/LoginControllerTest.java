package com.ignabasti.agricola.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;

class LoginControllerTest {
    @InjectMocks
    private LoginController loginController;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void login_debeRetornarVista() {
        String view = loginController.login();
        assertEquals("login", view);
    }
}
