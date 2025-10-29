package com.ignabasti.agricola.controller;

import com.ignabasti.agricola.model.Maquinaria;
import com.ignabasti.agricola.model.Usuario;
import com.ignabasti.agricola.repository.MaquinariaRepository;
import com.ignabasti.agricola.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Controller
public class MaquinariaController {
    @Autowired
    private MaquinariaRepository maquinariaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/maquinaria/buscar")
    public String buscarMaquinaria(@RequestParam(required = false) String tipo,
                                   @RequestParam(required = false) String ubicacion,
                                   @RequestParam(required = false) String fecha,
                                   @RequestParam(required = false) Double precio,
                                   Model model) {
        List<Maquinaria> maquinarias = maquinariaRepository.findAll();
        // Filtros simples en memoria (puedes optimizar con queries en el repo)
        if (tipo != null && !tipo.isEmpty()) {
            maquinarias.removeIf(m -> !m.getTipo().equalsIgnoreCase(tipo));
        }
        if (ubicacion != null && !ubicacion.isEmpty()) {
            maquinarias.removeIf(m -> !m.getUbicacion().equalsIgnoreCase(ubicacion));
        }
        if (fecha != null && !fecha.isEmpty()) {
            maquinarias.removeIf(m -> !m.getFecha_disponible().toString().equals(fecha));
        }
        if (precio != null) {
            maquinarias.removeIf(m -> m.getPrecio() > precio);
        }
        model.addAttribute("maquinarias", maquinarias);
        return "maquinaria_buscar";
    }

    @GetMapping("/maquinaria/registrar")
    public String mostrarFormularioRegistro() {
        return "maquinaria_registrar";
    }

    @org.springframework.web.bind.annotation.PostMapping("/maquinaria/registrar")
    public String registrarMaquinaria(
            @org.springframework.web.bind.annotation.RequestParam String tipo,
            @org.springframework.web.bind.annotation.RequestParam String marca,
            @org.springframework.web.bind.annotation.RequestParam String ubicacion,
            @org.springframework.web.bind.annotation.RequestParam java.sql.Date fecha_disponible,
            @org.springframework.web.bind.annotation.RequestParam Double precio,
            @org.springframework.web.bind.annotation.RequestParam Integer anio_fabricacion,
            @org.springframework.web.bind.annotation.RequestParam String capacidad,
            @org.springframework.web.bind.annotation.RequestParam String mantenciones,
            @org.springframework.web.bind.annotation.RequestParam String condiciones,
            @org.springframework.web.bind.annotation.RequestParam String medios_pago,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Maquinaria maq = new Maquinaria();
        maq.setTipo(tipo);
        maq.setMarca(marca);
        maq.setUbicacion(ubicacion);
        maq.setFecha_disponible(fecha_disponible);
        maq.setPrecio(precio);
        maq.setAnio_fabricacion(anio_fabricacion);
        maq.setCapacidad(capacidad);
        maq.setMantenciones(mantenciones);
        maq.setCondiciones(condiciones);
        maq.setMedios_pago(medios_pago);
        // Asociar usuario autenticado
        Usuario usuario = usuarioRepository.findByCorreo(userDetails.getUsername()).orElse(null);
        maq.setUsuario(usuario);
        maquinariaRepository.save(maq);
        return "redirect:/maquinaria/buscar";
    }
}
