package com.ignabasti.agricola.service;

import com.ignabasti.agricola.dto.MaquinariaDTO;
import com.ignabasti.agricola.model.Maquinaria;
import com.ignabasti.agricola.model.Usuario;
import com.ignabasti.agricola.repository.MaquinariaRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaquinariaServiceTest {

    @Mock
    private MaquinariaRepository maquinariaRepository;

    @Mock
    private AuthenticationHelper authenticationHelper;

    @InjectMocks
    private MaquinariaService maquinariaService;

    private Usuario usuario;
    private Maquinaria maquinaria;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1);
        usuario.setNombre("Pedro");
        usuario.setCorreo("pedro@mail.com");

        maquinaria = new Maquinaria();
        maquinaria.setId(10);
        maquinaria.setTipo("Tractor");
        maquinaria.setMarca("John Deere");
        maquinaria.setUbicacion("Talca");
        maquinaria.setFecha_disponible(Date.valueOf("2025-01-10"));
        maquinaria.setPrecio(500000);
        maquinaria.setAnio_fabricacion(2020);
        maquinaria.setCapacidad("Grande");
        maquinaria.setMantenciones("Al día");
        maquinaria.setCondiciones("Excelente");
        maquinaria.setMedios_pago("Efectivo");
        maquinaria.setUsuario(usuario);
    }

    // -------------------------------------------------------------
    // obtenerTodasLasMaquinarias()
    // -------------------------------------------------------------
    @Test
    void obtenerTodasLasMaquinarias_debeRetornarLista() {
        when(maquinariaRepository.findAll()).thenReturn(List.of(maquinaria));

        List<MaquinariaDTO> resultado = maquinariaService.obtenerTodasLasMaquinarias();

        assertEquals(1, resultado.size());
        assertEquals("Tractor", resultado.get(0).getTipo());
        verify(maquinariaRepository).findAll();
    }

    // -------------------------------------------------------------
    // obtenerMaquinariaPorId()
    // -------------------------------------------------------------
    @Test
    void obtenerMaquinariaPorId_encontrada() {
        when(maquinariaRepository.findById(10)).thenReturn(Optional.of(maquinaria));

        Optional<MaquinariaDTO> dto = maquinariaService.obtenerMaquinariaPorId(10);

        assertTrue(dto.isPresent());
        assertEquals("John Deere", dto.get().getMarca());
    }

    @Test
    void obtenerMaquinariaPorId_noEncontrada() {
        when(maquinariaRepository.findById(10)).thenReturn(Optional.empty());

        Optional<MaquinariaDTO> dto = maquinariaService.obtenerMaquinariaPorId(10);

        assertTrue(dto.isEmpty());
    }

    // -------------------------------------------------------------
    // buscarMaquinarias()
    // -------------------------------------------------------------
    @Test
    void buscarMaquinarias_filtraPorTipo() {
        when(maquinariaRepository.findAll()).thenReturn(List.of(maquinaria));

        List<MaquinariaDTO> resultado = maquinariaService.buscarMaquinarias("tractor", null, null, null);

        assertEquals(1, resultado.size());
        assertEquals("Tractor", resultado.get(0).getTipo());
    }

    @Test
    void buscarMaquinarias_filtraPorPrecioMaximo() {
        when(maquinariaRepository.findAll()).thenReturn(List.of(maquinaria));

        List<MaquinariaDTO> resultado = maquinariaService.buscarMaquinarias(null, null, null, 600000);

        assertEquals(1, resultado.size());
    }

    @Test
    void buscarMaquinarias_sinResultados() {
        when(maquinariaRepository.findAll()).thenReturn(List.of(maquinaria));

        List<MaquinariaDTO> resultado = maquinariaService.buscarMaquinarias(null, null, null, 100000);

        assertEquals(0, resultado.size());
    }

    // -------------------------------------------------------------
    // registrarMaquinaria()
    // -------------------------------------------------------------
    @Test
    void registrarMaquinaria_debeCrear() {
        when(authenticationHelper.obtenerUsuarioAutenticado()).thenReturn(usuario);
        when(maquinariaRepository.save(Mockito.any(Maquinaria.class))).thenReturn(maquinaria);

        MaquinariaDTO dto = MaquinariaDTO.builder()
                .tipo("Tractor")
                .marca("John Deere")
                .ubicacion("Talca")
                .fechaDisponible(Date.valueOf("2025-01-10"))
                .precio(500000)
                .anioFabricacion(2020)
                .capacidad("Grande")
                .mantenciones("Al día")
                .condiciones("Excelente")
                .mediosPago("Efectivo")
                .build();

        MaquinariaDTO resultado = maquinariaService.registrarMaquinaria(dto);

        assertEquals("Tractor", resultado.getTipo());
        verify(maquinariaRepository).save(any(Maquinaria.class));
    }

    // -------------------------------------------------------------
    // actualizarMaquinaria()
    // -------------------------------------------------------------
    @Test
    void actualizarMaquinaria_conPermiso() {
        when(authenticationHelper.obtenerUsuarioAutenticado()).thenReturn(usuario);
        when(maquinariaRepository.findById(10)).thenReturn(Optional.of(maquinaria));
        when(maquinariaRepository.save(maquinaria)).thenReturn(maquinaria);

        MaquinariaDTO dto = MaquinariaDTO.builder()
                .tipo("Nuevo Tractor")
                .marca("Kubota")
                .build();

        MaquinariaDTO resultado = maquinariaService.actualizarMaquinaria(10, dto);

        assertEquals("Nuevo Tractor", resultado.getTipo());
        assertEquals("Kubota", resultado.getMarca());
    }

    @Test
    void actualizarMaquinaria_sinPermisoDebeFallar() {
        Usuario otro = new Usuario();
        otro.setId(2);

        maquinaria.setUsuario(otro);

        when(authenticationHelper.obtenerUsuarioAutenticado()).thenReturn(usuario);
        when(maquinariaRepository.findById(10)).thenReturn(Optional.of(maquinaria));

        assertThrows(SecurityException.class,
                () -> maquinariaService.actualizarMaquinaria(10, new MaquinariaDTO()));
    }

    // -------------------------------------------------------------
    // eliminarMaquinaria()
    // -------------------------------------------------------------
    @Test
    void eliminarMaquinaria_conPermiso() {
        when(authenticationHelper.obtenerUsuarioAutenticado()).thenReturn(usuario);
        when(maquinariaRepository.findById(10)).thenReturn(Optional.of(maquinaria));

        maquinariaService.eliminarMaquinaria(10);

        verify(maquinariaRepository).delete(maquinaria);
    }

    @Test
    void eliminarMaquinaria_sinPermisoDebeFallar() {
        Usuario otro = new Usuario();
        otro.setId(5);

        maquinaria.setUsuario(otro);

        when(authenticationHelper.obtenerUsuarioAutenticado()).thenReturn(usuario);
        when(maquinariaRepository.findById(10)).thenReturn(Optional.of(maquinaria));

        assertThrows(SecurityException.class,
                () -> maquinariaService.eliminarMaquinaria(10));
    }
}