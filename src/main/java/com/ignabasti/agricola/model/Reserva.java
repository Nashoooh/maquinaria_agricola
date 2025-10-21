package com.ignabasti.agricola.model;

import jakarta.persistence.*;

@Entity
@Table(name = "reserva")
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "maquinaria_id")
    private Maquinaria maquinaria;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    private java.sql.Date fecha_reserva;

    // Getters y setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Maquinaria getMaquinaria() {
        return maquinaria;
    }

    public void setMaquinaria(Maquinaria maquinaria) {
        this.maquinaria = maquinaria;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public java.sql.Date getFecha_reserva() {
        return fecha_reserva;
    }

    public void setFecha_reserva(java.sql.Date fecha_reserva) {
        this.fecha_reserva = fecha_reserva;
    }
}
