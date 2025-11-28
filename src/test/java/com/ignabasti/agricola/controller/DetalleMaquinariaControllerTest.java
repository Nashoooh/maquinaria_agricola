package com.ignabasti.agricola.controller;

import com.ignabasti.agricola.dto.MaquinariaDTO;
import com.ignabasti.agricola.service.MaquinariaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.List;
import java.util.Optional;

class DetalleMaquinariaControllerTest {
    @Mock
    private MaquinariaService maquinariaService;
    @Mock
    private Model model;
    @InjectMocks
    private DetalleMaquinariaController detalleMaquinariaController;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void verDetalle_debeAgregarMaquinariaYRetornarVista() {
        MaquinariaDTO dto = new MaquinariaDTO();
        dto.setTipo("Tractor");
        Optional<MaquinariaDTO> opt = Optional.of(dto);
        when(maquinariaService.obtenerMaquinariaPorId(anyInt())).thenReturn(opt);
        String view = detalleMaquinariaController.verDetalle(1, model);
        verify(model).addAttribute("maquinaria", dto);
        assertEquals("maquinaria_detalle", view);
    }

    @Test
    void detalleMaquinarias_sinIdAgregaListaYRetornaVista() {
        List<MaquinariaDTO> lista = List.of(new MaquinariaDTO());
        when(maquinariaService.obtenerTodasLasMaquinarias()).thenReturn(lista);
        String view = detalleMaquinariaController.detalleMaquinarias(null, model);
        verify(model).addAttribute("maquinarias", lista);
        assertEquals("maquinaria_detalle", view);
    }

    @Test
    void detalleMaquinarias_conIdAgregaSeleccionadaYRetornaVista() {
        List<MaquinariaDTO> lista = List.of(new MaquinariaDTO());
        MaquinariaDTO dto = new MaquinariaDTO();
        Optional<MaquinariaDTO> opt = Optional.of(dto);
        when(maquinariaService.obtenerTodasLasMaquinarias()).thenReturn(lista);
        when(maquinariaService.obtenerMaquinariaPorId(anyInt())).thenReturn(opt);
        String view = detalleMaquinariaController.detalleMaquinarias(1, model);
        verify(model).addAttribute("maquinarias", lista);
        verify(model).addAttribute("maquinariaSeleccionada", dto);
        assertEquals("maquinaria_detalle", view);
    }
}
