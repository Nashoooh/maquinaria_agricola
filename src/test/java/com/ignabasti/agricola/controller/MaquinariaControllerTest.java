package com.ignabasti.agricola.controller;

import com.ignabasti.agricola.dto.MaquinariaDTO;
import com.ignabasti.agricola.service.MaquinariaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class MaquinariaControllerTest {
    @Mock
    private MaquinariaService maquinariaService;

    @Mock
    private Model model;

    @InjectMocks
    private MaquinariaController maquinariaController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void buscarMaquinaria_debeAgregarListaAlModeloYRetornarVista() {
        List<MaquinariaDTO> lista = List.of(MaquinariaDTO.builder().tipo("Tractor").build());
        when(maquinariaService.buscarMaquinarias(any(), any(), any(), any())).thenReturn(lista);

        String view = maquinariaController.buscarMaquinaria("Tractor", "Talca", "2025-01-10", 500000, model);

        verify(maquinariaService).buscarMaquinarias("Tractor", "Talca", "2025-01-10", 500000);
        verify(model).addAttribute("maquinarias", lista);
        assertEquals("maquinaria_buscar", view);
    }

    @Test
    void mostrarFormularioRegistro_debeRetornarVista() {
        String view = maquinariaController.mostrarFormularioRegistro();
        assertEquals("maquinaria_registrar", view);
    }

    @Test
    void registrarMaquinaria_exitoRedirigeYAgregaMensaje() {
        when(maquinariaService.registrarMaquinaria(any())).thenReturn(MaquinariaDTO.builder().tipo("Tractor").build());

        String view = maquinariaController.registrarMaquinaria(
                "Tractor", "John Deere", "Talca", Date.valueOf("2025-01-10"), 500000, 2020,
                "Grande", "Al día", "Excelente", "Efectivo", model);

        verify(maquinariaService).registrarMaquinaria(any(MaquinariaDTO.class));
        verify(model).addAttribute(eq("exito"), anyString());
        assertEquals("redirect:/maquinaria/buscar", view);
    }

    @Test
    void registrarMaquinaria_securityExceptionRetornaError() {
        when(maquinariaService.registrarMaquinaria(any())).thenThrow(new SecurityException());

        String view = maquinariaController.registrarMaquinaria(
                "Tractor", "John Deere", "Talca", Date.valueOf("2025-01-10"), 500000, 2020,
                "Grande", "Al día", "Excelente", "Efectivo", model);

        verify(model).addAttribute(eq("error"), anyString());
        assertEquals("maquinaria_registrar", view);
    }

    @Test
    void registrarMaquinaria_exceptionRetornaError() {
        when(maquinariaService.registrarMaquinaria(any())).thenThrow(new RuntimeException("Fallo"));

        String view = maquinariaController.registrarMaquinaria(
                "Tractor", "John Deere", "Talca", Date.valueOf("2025-01-10"), 500000, 2020,
                "Grande", "Al día", "Excelente", "Efectivo", model);

        verify(model).addAttribute(eq("error"), contains("Fallo"));
        assertEquals("maquinaria_registrar", view);
    }
}
