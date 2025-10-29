package com.ignabasti.agricola.model;

import jakarta.persistence.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nombre;
    private String correo;
    private String contrasena;
    private String direccion;
    private String telefono;
    private String cultivos;

    @OneToMany(mappedBy = "usuario")
    @JsonManagedReference
    private List<Maquinaria> maquinarias;

    @OneToMany(mappedBy = "usuario")
    @JsonManagedReference
    private List<Aviso> avisos;

    @OneToMany(mappedBy = "usuario")
    @JsonManagedReference
    private List<Reserva> reservas;

    // Getters y setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCultivos() {
        return cultivos;
    }

    public void setCultivos(String cultivos) {
        this.cultivos = cultivos;
    }

    public java.util.List<Maquinaria> getMaquinarias() {
        return maquinarias;
    }

    public void setMaquinarias(java.util.List<Maquinaria> maquinarias) {
        this.maquinarias = maquinarias;
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
