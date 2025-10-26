package com.ignabasti.agricola.controller;

import com.ignabasti.agricola.model.Maquinaria;
import com.ignabasti.agricola.repository.MaquinariaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
}
