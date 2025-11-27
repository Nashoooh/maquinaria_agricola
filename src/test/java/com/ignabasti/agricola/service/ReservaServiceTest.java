package com.ignabasti.agricola.service;

import com.ignabasti.agricola.dto.ReservaDTO;
import com.ignabasti.agricola.model.Maquinaria;
import com.ignabasti.agricola.model.Reserva;
import com.ignabasti.agricola.model.Usuario;
import com.ignabasti.agricola.repository.MaquinariaRepository;
import com.ignabasti.agricola.repository.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private MaquinariaRepository maquinariaRepository;

    @Mock
    private AuthenticationHelper authenticationHelper;

    @InjectMocks
    private ReservaService reservaService;

    private Usuario usuario;
    private Maquinaria maquinaria;
    private Reserva reserva;

    @BeforeEach
    void setup() {
        usuario = new Usuario();
        usuario.setId(1);
        usuario.setCorreo("test@correo.com");
        usuario.setNombre("Juan");

        maquinaria = new Maquinaria();
        maquinaria.setId(10);
        maquinaria.setTipo("Tractor");
        maquinaria.setMarca("John Deere");

        reserva = new Reserva();
        reserva.setId(100);
        reserva.setMaquinaria(maquinaria);
        reserva.setUsuario(usuario);
        reserva.setFecha_reserva(java.sql.Date.valueOf(LocalDate.now()));
    }

    @Test
    void testObtenerTodasLasReservas() {
        when(reservaRepository.findAll()).thenReturn(List.of(reserva));

        List<ReservaDTO> resultado = reservaService.obtenerTodasLasReservas();

        assertEquals(1, resultado.size());
        assertEquals(100, resultado.get(0).getId());
        verify(reservaRepository).findAll();
    }

    @Test
    void testObtenerReservasDelUsuarioActual() {
        when(authenticationHelper.obtenerUsuarioAutenticado()).thenReturn(usuario);
        when(reservaRepository.findAll()).thenReturn(List.of(reserva));

        List<ReservaDTO> resultado = reservaService.obtenerReservasDelUsuarioActual();

        assertEquals(1, resultado.size());
        assertEquals(usuario.getId(), resultado.get(0).getUsuarioId());
    }

    @Test
    void testCrearReserva() {
        ReservaDTO dto = ReservaDTO.builder()
                .maquinariaId(10)
                .fechaReserva(java.sql.Date.valueOf(LocalDate.now()))
                .build();

        when(authenticationHelper.obtenerUsuarioAutenticado()).thenReturn(usuario);
        when(maquinariaRepository.findById(10)).thenReturn(Optional.of(maquinaria));
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

        ReservaDTO resultado = reservaService.crearReserva(dto);

        assertNotNull(resultado);
        assertEquals(100, resultado.getId());
        assertEquals(usuario.getId(), resultado.getUsuarioId());
        assertEquals(maquinaria.getId(), resultado.getMaquinariaId());

        verify(reservaRepository).save(any(Reserva.class));
    }

    @Test
    void testCrearReserva_MaquinariaNoEncontrada() {
        ReservaDTO dto = ReservaDTO.builder().maquinariaId(99).build();

        when(authenticationHelper.obtenerUsuarioAutenticado()).thenReturn(usuario);
        when(maquinariaRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> reservaService.crearReserva(dto));
    }

    @Test
    void testEliminarReservaCorrectamente() {
        when(authenticationHelper.obtenerUsuarioAutenticado()).thenReturn(usuario);
        when(reservaRepository.findById(100)).thenReturn(Optional.of(reserva));

        reservaService.eliminarReserva(100);

        verify(reservaRepository).delete(reserva);
    }

    @Test
    void testEliminarReserva_SinPermisos() {
        Usuario otroUsuario = new Usuario();
        otroUsuario.setId(999);

        reserva.setUsuario(otroUsuario);

        when(authenticationHelper.obtenerUsuarioAutenticado()).thenReturn(usuario);
        when(reservaRepository.findById(100)).thenReturn(Optional.of(reserva));

        assertThrows(SecurityException.class,
                () -> reservaService.eliminarReserva(100));
    }

    @Test
    void testEliminarReserva_NoExiste() {
        when(reservaRepository.findById(100)).thenReturn(Optional.empty());
        when(authenticationHelper.obtenerUsuarioAutenticado()).thenReturn(usuario);

        assertThrows(IllegalArgumentException.class,
                () -> reservaService.eliminarReserva(100));
    }
}