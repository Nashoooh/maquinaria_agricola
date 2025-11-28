package com.ignabasti.agricola.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;

class HomeControllerTest {
    @InjectMocks
    private HomeController homeController;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void home_debeRetornarVista() {
        String view = homeController.home();
        assertEquals("home", view);
    }
}
