package com.ignabasti.agricola.controller;

import com.ignabasti.agricola.dto.MaquinariaDTO;
import com.ignabasti.agricola.dto.ReservaDTO;
import com.ignabasti.agricola.service.MaquinariaService;
import com.ignabasti.agricola.service.ReservaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservaControllerTest {
    @Mock
    private MaquinariaService maquinariaService;
    @Mock
    private ReservaService reservaService;
    @Mock
    private Model model;
    @InjectMocks
    private ReservaController reservaController;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void mostrarFormularioReserva_debeAgregarMaquinariasYRetornarVista() {
        List<MaquinariaDTO> lista = List.of(MaquinariaDTO.builder().tipo("Tractor").build());
        when(maquinariaService.obtenerTodasLasMaquinarias()).thenReturn(lista);

        String view = reservaController.mostrarFormularioReserva(model);

        verify(maquinariaService).obtenerTodasLasMaquinarias();
        verify(model).addAttribute("maquinarias", lista);
        assertEquals("maquinaria_reserva", view);
    }

    @Test
    void reservarMaquinaria_exitoAgregaMensajeYRetornaVista() {
        List<MaquinariaDTO> lista = List.of(MaquinariaDTO.builder().tipo("Tractor").build());
        when(maquinariaService.obtenerTodasLasMaquinarias()).thenReturn(lista);

        String view = reservaController.reservarMaquinaria(1, "2025-01-10", model);

        verify(reservaService).crearReserva(any(ReservaDTO.class));
        verify(model).addAttribute(eq("exito"), anyString());
        verify(model).addAttribute("maquinarias", lista);
        assertEquals("maquinaria_reserva", view);
    }
}