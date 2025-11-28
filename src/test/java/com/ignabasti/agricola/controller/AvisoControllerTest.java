package com.ignabasti.agricola.controller;

import com.ignabasti.agricola.dto.AvisoDTO;
import com.ignabasti.agricola.dto.MaquinariaDTO;
import com.ignabasti.agricola.service.AvisoService;
import com.ignabasti.agricola.service.MaquinariaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AvisoControllerTest {
    @Mock
    private AvisoService avisoService;
    @Mock
    private MaquinariaService maquinariaService;
    @Mock
    private Model model;
    @InjectMocks
    private AvisoController avisoController;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void mostrarFormularioAviso_debeAgregarMaquinariasYRetornarVista() {
        List<MaquinariaDTO> lista = List.of(MaquinariaDTO.builder().tipo("Tractor").build());
        when(maquinariaService.obtenerTodasLasMaquinarias()).thenReturn(lista);
        String view = avisoController.mostrarFormularioAviso(model);
        verify(maquinariaService).obtenerTodasLasMaquinarias();
        verify(model).addAttribute("maquinarias", lista);
        assertEquals("maquinaria_avisos", view);
    }

    @Test
    void publicarAviso_exitoAgregaMensajeYRetornaVista() {
        List<MaquinariaDTO> lista = List.of(MaquinariaDTO.builder().tipo("Tractor").build());
        when(maquinariaService.obtenerTodasLasMaquinarias()).thenReturn(lista);
        String view = avisoController.publicarAviso(1, true, model);
        verify(avisoService).publicarAviso(any(AvisoDTO.class));
        verify(model).addAttribute(eq("exito"), anyString());
        verify(model).addAttribute("maquinarias", lista);
        assertEquals("maquinaria_avisos", view);
    }

    @Test
    void publicarAviso_securityExceptionAgregaErrorYRetornaVista() {
        List<MaquinariaDTO> lista = List.of(MaquinariaDTO.builder().tipo("Tractor").build());
        when(maquinariaService.obtenerTodasLasMaquinarias()).thenReturn(lista);
        doThrow(new SecurityException()).when(avisoService).publicarAviso(any(AvisoDTO.class));
        String view = avisoController.publicarAviso(1, true, model);
        verify(model).addAttribute(eq("error"), anyString());
        verify(model).addAttribute("maquinarias", lista);
        assertEquals("maquinaria_avisos", view);
    }

    @Test
    void publicarAviso_exceptionAgregaErrorYRetornaVista() {
        List<MaquinariaDTO> lista = List.of(MaquinariaDTO.builder().tipo("Tractor").build());
        when(maquinariaService.obtenerTodasLasMaquinarias()).thenReturn(lista);
        doThrow(new RuntimeException("Fallo")).when(avisoService).publicarAviso(any(AvisoDTO.class));
        String view = avisoController.publicarAviso(1, true, model);
        verify(model).addAttribute(eq("error"), contains("Fallo"));
        verify(model).addAttribute("maquinarias", lista);
        assertEquals("maquinaria_avisos", view);
    }
}
