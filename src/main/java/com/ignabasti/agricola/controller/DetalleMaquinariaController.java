package com.ignabasti.agricola.controller;

import com.ignabasti.agricola.model.Maquinaria;
import com.ignabasti.agricola.repository.MaquinariaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class DetalleMaquinariaController {
    @Autowired
    private MaquinariaRepository maquinariaRepository;

    @GetMapping("/maquinaria/detalle/{id}")
    public String verDetalle(@PathVariable Integer id, Model model) {
        Maquinaria maquinaria = maquinariaRepository.findById(id).orElse(null);
        model.addAttribute("maquinaria", maquinaria);
        return "maquinaria_detalle";
    }

    @GetMapping("/maquinaria/detalle")
    public String detalleMaquinarias(@org.springframework.web.bind.annotation.RequestParam(required = false) Integer id, Model model) {
        List<Maquinaria> lista = maquinariaRepository.findAll();
        model.addAttribute("maquinarias", lista);
        
        // Si se proporciona un ID, buscar y mostrar ese detalle
        if (id != null) {
            Maquinaria maquinaria = maquinariaRepository.findById(id).orElse(null);
            model.addAttribute("maquinariaSeleccionada", maquinaria);
        }
        
        return "maquinaria_detalle";
    }
}
