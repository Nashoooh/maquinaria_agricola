package com.ignabasti.agricola.model;

import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MaquinariaTest {

    @Test
    void gettersAndSetters_debenFuncionarCorrectamente() {
        Maquinaria maquinaria = new Maquinaria();

        Usuario usuario = new Usuario();
        Aviso aviso = new Aviso();
        Reserva reserva = new Reserva();

        Date fecha = Date.valueOf("2025-01-01");

        maquinaria.setId(1);
        maquinaria.setTipo("Tractor");
        maquinaria.setUbicacion("Campo");
        maquinaria.setFecha_disponible(fecha);
        maquinaria.setPrecio(1000);
        maquinaria.setMarca("John Deere");
        maquinaria.setAnio_fabricacion(2020);
        maquinaria.setCapacidad("5 toneladas");
        maquinaria.setMantenciones("Al día");
        maquinaria.setCondiciones("Buenas");
        maquinaria.setMedios_pago("Transferencia");

        maquinaria.setUsuario(usuario);
        maquinaria.setAvisos(List.of(aviso));
        maquinaria.setReservas(List.of(reserva));

        assertEquals(1, maquinaria.getId());
        assertEquals("Tractor", maquinaria.getTipo());
        assertEquals("Campo", maquinaria.getUbicacion());
        assertEquals(fecha, maquinaria.getFecha_disponible());
        assertEquals(1000, maquinaria.getPrecio());
        assertEquals("John Deere", maquinaria.getMarca());
        assertEquals(2020, maquinaria.getAnio_fabricacion());
        assertEquals("5 toneladas", maquinaria.getCapacidad());
        assertEquals("Al día", maquinaria.getMantenciones());
        assertEquals("Buenas", maquinaria.getCondiciones());
        assertEquals("Transferencia", maquinaria.getMedios_pago());

        assertEquals(usuario, maquinaria.getUsuario());
        assertEquals(1, maquinaria.getAvisos().size());
        assertEquals(1, maquinaria.getReservas().size());
    }
}