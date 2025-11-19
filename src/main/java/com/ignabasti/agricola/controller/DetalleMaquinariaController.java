package com.ignabasti.agricola.controller;

import com.ignabasti.agricola.dto.MaquinariaDTO;
import com.ignabasti.agricola.service.MaquinariaService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class DetalleMaquinariaController {

    private final MaquinariaService maquinariaService;

    @GetMapping("/maquinaria/detalle/{id}")
    public String verDetalle(@PathVariable Integer id, Model model) {
        Optional<MaquinariaDTO> maquinaria = maquinariaService.obtenerMaquinariaPorId(id);
        model.addAttribute("maquinaria", maquinaria.orElse(null));
        return "maquinaria_detalle";
    }

    @GetMapping("/maquinaria/detalle")
    public String detalleMaquinarias(@RequestParam(required = false) Integer id, Model model) {
        List<MaquinariaDTO> lista = maquinariaService.obtenerTodasLasMaquinarias();
        model.addAttribute("maquinarias", lista);
        
        // Si se proporciona un ID, buscar y mostrar ese detalle
        if (id != null) {
            Optional<MaquinariaDTO> maquinaria = maquinariaService.obtenerMaquinariaPorId(id);
            model.addAttribute("maquinariaSeleccionada", maquinaria.orElse(null));
        }
        
        return "maquinaria_detalle";
    }
}
