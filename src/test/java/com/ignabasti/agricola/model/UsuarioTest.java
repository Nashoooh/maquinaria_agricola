package com.ignabasti.agricola.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {

    @Test
    void gettersAndSetters_debenFuncionarCorrectamente() {
        Usuario usuario = new Usuario();

        Maquinaria maquinaria = new Maquinaria();
        Aviso aviso = new Aviso();
        Reserva reserva = new Reserva();

        usuario.setId(1);
        usuario.setNombre("Juan");
        usuario.setCorreo("juan@mail.com");
        usuario.setContrasena("123456");
        usuario.setDireccion("Calle 123");
        usuario.setTelefono("123456789");
        usuario.setCultivos("Trigo");

        usuario.setMaquinarias(List.of(maquinaria));
        usuario.setAvisos(List.of(aviso));
        usuario.setReservas(List.of(reserva));

        assertEquals(1, usuario.getId());
        assertEquals("Juan", usuario.getNombre());
        assertEquals("juan@mail.com", usuario.getCorreo());
        assertEquals("123456", usuario.getContrasena());
        assertEquals("Calle 123", usuario.getDireccion());
        assertEquals("123456789", usuario.getTelefono());
        assertEquals("Trigo", usuario.getCultivos());

        assertEquals(1, usuario.getMaquinarias().size());
        assertEquals(1, usuario.getAvisos().size());
        assertEquals(1, usuario.getReservas().size());
    }
}