package com.ignabasti.agricola.model;

import jakarta.persistence.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "maquinaria")
public class Maquinaria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String tipo;
    private String ubicacion;
    private java.sql.Date fecha_disponible;
    private Integer precio;
    private String marca;
    private Integer anio_fabricacion;
    private String capacidad;
    private String mantenciones;
    private String condiciones;
    private String medios_pago;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @JsonBackReference
    private Usuario usuario;

    @OneToMany(mappedBy = "maquinaria")
    @JsonManagedReference
    private List<Aviso> avisos;

    @OneToMany(mappedBy = "maquinaria")
    @JsonManagedReference
    private List<Reserva> reservas;

    // Getters y setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public java.sql.Date getFecha_disponible() {
        return fecha_disponible;
    }

    public void setFecha_disponible(java.sql.Date fecha_disponible) {
        this.fecha_disponible = fecha_disponible;
    }

    public Integer getPrecio() {
        return precio;
    }

    public void setPrecio(Integer precio) {
        this.precio = precio;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public Integer getAnio_fabricacion() {
        return anio_fabricacion;
    }

    public void setAnio_fabricacion(Integer anio_fabricacion) {
        this.anio_fabricacion = anio_fabricacion;
    }

    public String getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(String capacidad) {
        this.capacidad = capacidad;
    }

    public String getMantenciones() {
        return mantenciones;
    }

    public void setMantenciones(String mantenciones) {
        this.mantenciones = mantenciones;
    }

    public String getCondiciones() {
        return condiciones;
    }

    public void setCondiciones(String condiciones) {
        this.condiciones = condiciones;
    }

    public String getMedios_pago() {
        return medios_pago;
    }

    public void setMedios_pago(String medios_pago) {
        this.medios_pago = medios_pago;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public java.util.List<Aviso> getAvisos() {
        return avisos;
    }

    public void setAvisos(java.util.List<Aviso> avisos) {
        this.avisos = avisos;
    }

    public java.util.List<Reserva> getReservas() {
        return reservas;
    }

    public void setReservas(java.util.List<Reserva> reservas) {
        this.reservas = reservas;
    }
}
