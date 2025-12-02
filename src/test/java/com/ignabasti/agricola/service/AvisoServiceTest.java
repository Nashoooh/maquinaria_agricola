package com.ignabasti.agricola.service;

import com.ignabasti.agricola.dto.AvisoDTO;
import com.ignabasti.agricola.model.Aviso;
import com.ignabasti.agricola.model.Maquinaria;
import com.ignabasti.agricola.model.Usuario;
import com.ignabasti.agricola.repository.AvisoRepository;
import com.ignabasti.agricola.repository.MaquinariaRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvisoServiceTest {

    @Mock
    private AvisoRepository avisoRepository;

    @Mock
    private MaquinariaRepository maquinariaRepository;

    @Mock
    private AuthenticationHelper authenticationHelper;

    @InjectMocks
    private AvisoService avisoService;

    private Usuario usuario;
    private Maquinaria maquinaria;
    private Aviso aviso;

    @BeforeEach
    void setUp() {

        usuario = new Usuario();
        usuario.setId(1);
        usuario.setNombre("Pedro");
        usuario.setCorreo("pedro@mail.com");

        maquinaria = new Maquinaria();
        maquinaria.setId(100);
        maquinaria.setTipo("Tractor");
        maquinaria.setMarca("John Deere");

        aviso = new Aviso();
        aviso.setId(50);
        aviso.setUsuario(usuario);
        aviso.setMaquinaria(maquinaria);
        aviso.setDestacado(true);
        aviso.setFecha_publicacion(Date.valueOf("2025-01-10"));
    }

    // -------------------------------------------------------------
    // obtenerTodosLosAvisos()
    // -------------------------------------------------------------
    @Test
    void obtenerTodosLosAvisos_debeRetornarLista() {
        when(avisoRepository.findAll()).thenReturn(List.of(aviso));

        List<AvisoDTO> result = avisoService.obtenerTodosLosAvisos();

        assertEquals(1, result.size());
        assertEquals(50, result.get(0).getId());
        verify(avisoRepository).findAll();
    }

    // -------------------------------------------------------------
    // publicarAviso()
    // -------------------------------------------------------------
    @Test
    void publicarAviso_debeCrearCorrectamente() {
        AvisoDTO dto = AvisoDTO.builder()
                .maquinariaId(100)
                .destacado(true)
                .build();

        when(authenticationHelper.obtenerUsuarioAutenticado()).thenReturn(usuario);
        when(maquinariaRepository.findById(100)).thenReturn(Optional.of(maquinaria));
        when(avisoRepository.save(Mockito.any(Aviso.class))).thenReturn(aviso);

        AvisoDTO result = avisoService.publicarAviso(dto);

        assertEquals(50, result.getId());
        assertEquals(100, result.getMaquinariaId());
        verify(avisoRepository).save(any(Aviso.class));
    }

    @Test
    void publicarAviso_maquinariaNoExisteDebeFallar() {
        AvisoDTO dto = AvisoDTO.builder()
                .maquinariaId(999)
                .build();

        when(authenticationHelper.obtenerUsuarioAutenticado()).thenReturn(usuario);
        when(maquinariaRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> avisoService.publicarAviso(dto));
    }

    // -------------------------------------------------------------
    // eliminarAviso()
    // -------------------------------------------------------------
    @Test
    void eliminarAviso_conPermiso() {
        when(authenticationHelper.obtenerUsuarioAutenticado()).thenReturn(usuario);
        when(avisoRepository.findById(50)).thenReturn(Optional.of(aviso));

        avisoService.eliminarAviso(50);

        verify(avisoRepository).delete(aviso);
    }

    @Test
    void eliminarAviso_sinPermisoDebeFallar() {
        Usuario otro = new Usuario();
        otro.setId(2);

        aviso.setUsuario(otro);

        when(authenticationHelper.obtenerUsuarioAutenticado()).thenReturn(usuario);
        when(avisoRepository.findById(50)).thenReturn(Optional.of(aviso));

        assertThrows(SecurityException.class,
                () -> avisoService.eliminarAviso(50));
    }

    @Test
    void eliminarAviso_noExisteDebeFallar() {
        when(authenticationHelper.obtenerUsuarioAutenticado()).thenReturn(usuario);
        when(avisoRepository.findById(50)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> avisoService.eliminarAviso(50));
    }
}