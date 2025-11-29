package com.ignabasti.agricola.service;

import com.ignabasti.agricola.dto.AvisoDTO;
import com.ignabasti.agricola.dto.MaquinariaDTO;
import com.ignabasti.agricola.model.Aviso;
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
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    private Maquinaria m1;
    private Maquinaria m2;

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

        m1 = new Maquinaria();
        m1.setTipo("tractor");
        m1.setUbicacion("campo grande");
        m1.setFecha_disponible(Date.valueOf("2024-11-30"));
        m1.setPrecio(50000);

        m2 = new Maquinaria();
        m2.setTipo("cosechadora");
        m2.setUbicacion("ciudad norte");
        m2.setFecha_disponible(Date.valueOf("2024-11-30"));
        m2.setPrecio(200000);
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

    // Cobertura en funciones
    @Test
    void buscarMaquinarias_sinTipo() {
        when(maquinariaRepository.findAll()).thenReturn(List.of(m1, m2));

        List<MaquinariaDTO> result = maquinariaService.buscarMaquinarias(null, null, null, null);

        assertEquals(2, result.size());
    }

    @Test
    void buscarMaquinarias_conTipo() {
        when(maquinariaRepository.findAll()).thenReturn(List.of(m1, m2));

        List<MaquinariaDTO> result = maquinariaService.buscarMaquinarias("tractor", null, null, null);

        assertEquals(1, result.size());
        assertEquals("tractor", result.get(0).getTipo());
    }

    @Test
    void buscarMaquinarias_conUbicacion() {
        when(maquinariaRepository.findAll()).thenReturn(List.of(m1, m2));

        List<MaquinariaDTO> result = maquinariaService.buscarMaquinarias(null, "campo", null, null);

        assertEquals(1, result.size());
        assertTrue(result.get(0).getUbicacion().contains("campo"));
    }

    @Test
    void buscarMaquinarias_conFecha() {
        when(maquinariaRepository.findAll()).thenReturn(List.of(m1, m2));

        List<MaquinariaDTO> result = maquinariaService.buscarMaquinarias(null, null, "2024-11-30", null);

        assertEquals(2, result.size());
    }

    @Test
    void buscarMaquinarias_conPrecioMaximo() {
        when(maquinariaRepository.findAll()).thenReturn(List.of(m1, m2));

        List<MaquinariaDTO> result = maquinariaService.buscarMaquinarias(null, null, null, 100000);

        assertEquals(1, result.size()); // solo m1 entra
        assertEquals(50000, result.get(0).getPrecio());
    }

    // Cobertura en if

    //if (tipo != null && !tipo.isEmpty()) tipo NO es nulo / tipo NO está vacío / PERO ninguna maquinaria coincide
    @Test
    void buscarMaquinarias_tipoNoCoincide() {
        when(maquinariaRepository.findAll()).thenReturn(List.of(m1, m2));

        List<MaquinariaDTO> result = maquinariaService.buscarMaquinarias("excavadora", null, null, null);

        assertTrue(result.isEmpty());
    }

    // Ubicacion viene con valor / PERO la maquinaria tiene ubicacion = null
    @Test
    void buscarMaquinarias_ubicacionNullNoCoincide() {
        m1.setUbicacion(null);
        when(maquinariaRepository.findAll()).thenReturn(List.of(m1));

        List<MaquinariaDTO> result = maquinariaService.buscarMaquinarias(null, "campo", null, null);

        assertTrue(result.isEmpty());
    }

    // Falta en el lambda interno de ubicación (cuando NO contiene el texto)
    @Test
    void buscarMaquinarias_ubicacionNoContiene() {
        when(maquinariaRepository.findAll()).thenReturn(List.of(m2)); // "ciudad norte"

        List<MaquinariaDTO> result = maquinariaService.buscarMaquinarias(null, "sur", null, null);

        assertTrue(result.isEmpty());
    }

    // Fecha tiene valor / maquinaria tiene fecha_disponible = null
    @Test
    void buscarMaquinarias_fechaPeroMaquinariaSinFecha() {
        m1.setFecha_disponible(null);
        when(maquinariaRepository.findAll()).thenReturn(List.of(m1));

        List<MaquinariaDTO> result = maquinariaService.buscarMaquinarias(null, null, "2025-01-01", null);

        assertTrue(result.isEmpty());
    }

    // Falta en el lambda fecha — fecha distinta
    @Test
    void buscarMaquinarias_fechaDistinta() {
        m1.setFecha_disponible(Date.valueOf("2025-01-01"));
        when(maquinariaRepository.findAll()).thenReturn(List.of(m1));

        List<MaquinariaDTO> result = maquinariaService.buscarMaquinarias(null, null, "2025-02-01", null);

        assertTrue(result.isEmpty());
    }

    // Falta en el lambda fecha — formato no coincide / El servicio compara toString(), así que si el toString() no coincide, hay que cubrir esa rama:
    @Test
    void buscarMaquinarias_fechaFormatoNoCoincide() {
        m1.setFecha_disponible(Date.valueOf("2025-01-01"));
        when(maquinariaRepository.findAll()).thenReturn(List.of(m1));

        // formato no igual al toString() de Date ("2025-01-01")
        List<MaquinariaDTO> result = maquinariaService.buscarMaquinarias(null, null, "01-01-2025", null);

        assertTrue(result.isEmpty());
    }

    // Falta en el lambda precio — maquinaria.precio = null
    @Test
    void buscarMaquinarias_precioNullNoCoincide() {
        m1.setPrecio(null);
        when(maquinariaRepository.findAll()).thenReturn(List.of(m1));

        List<MaquinariaDTO> result = maquinariaService.buscarMaquinarias(null, null, null, 100000);

        assertTrue(result.isEmpty());
    }

    // Test donde tipo es vacío o null, y el código NO entre.
    @Test
    void buscarMaquinarias_tipoVacio_NoEntraIf() {
        when(maquinariaRepository.findAll()).thenReturn(List.of(m1, m2));

        List<MaquinariaDTO> result = maquinariaService.buscarMaquinarias("", null, null, null);

        assertEquals(2, result.size()); // no filtra nada
    }

    @Test
    void buscarMaquinarias_ubicacionVacia_NoEntraIf() {
        when(maquinariaRepository.findAll()).thenReturn(List.of(m1, m2));

        List<MaquinariaDTO> result = maquinariaService.buscarMaquinarias(null, "", null, null);

        assertEquals(2, result.size());
    }

    @Test
    void buscarMaquinarias_fechaVacia_NoEntraIf() {
        when(maquinariaRepository.findAll()).thenReturn(List.of(m1, m2));

        List<MaquinariaDTO> result = maquinariaService.buscarMaquinarias(null, null, "", null);

        assertEquals(2, result.size());
    }

    // Cobertura en excepciones
    @Test
    void actualizarMaquinaria_idInexistente_debeLanzarIllegalArgumentException() {
        Integer idInexistente = 999;

        when(authenticationHelper.obtenerUsuarioAutenticado()).thenReturn(usuario);
        when(maquinariaRepository.findById(idInexistente)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> maquinariaService.actualizarMaquinaria(idInexistente, new MaquinariaDTO())
        );

        assertTrue(ex.getMessage().contains("Maquinaria no encontrada con ID"));
    }

    @Test
    void eliminarMaquinaria_idInexistente_debeLanzarIllegalArgumentException() {
        Integer idInexistente = 999;

        when(authenticationHelper.obtenerUsuarioAutenticado()).thenReturn(usuario);
        when(maquinariaRepository.findById(idInexistente)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> maquinariaService.eliminarMaquinaria(idInexistente)
        );

        assertTrue(ex.getMessage().contains("Maquinaria no encontrada con ID"));
    }
}