package com.ignabasti.agricola.controller;

import com.ignabasti.agricola.model.Maquinaria;
import com.ignabasti.agricola.repository.MaquinariaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Controller
public class MaquinariaController {
    @Autowired
    private MaquinariaRepository maquinariaRepository;

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
        return "buscar_maquinaria";
    }
}
