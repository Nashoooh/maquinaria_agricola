package com.ignabasti.agricola.controller;

import com.ignabasti.agricola.dto.MaquinariaDTO;
import com.ignabasti.agricola.service.MaquinariaService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MaquinariaController {

    private final MaquinariaService maquinariaService;

    @GetMapping("/maquinaria/buscar")
    public String buscarMaquinaria(@RequestParam(required = false) String tipo,
                                   @RequestParam(required = false) String ubicacion,
                                   @RequestParam(required = false) String fecha,
                                   @RequestParam(required = false) Integer precio,
                                   Model model) {
        List<MaquinariaDTO> maquinarias = maquinariaService.buscarMaquinarias(tipo, ubicacion, fecha, precio);
        model.addAttribute("maquinarias", maquinarias);
        return "maquinaria_buscar";
    }

    @GetMapping("/maquinaria/registrar")
    public String mostrarFormularioRegistro() {
        return "maquinaria_registrar";
    }

    @PostMapping("/maquinaria/registrar")
    public String registrarMaquinaria(
            @RequestParam String tipo,
            @RequestParam String marca,
            @RequestParam String ubicacion,
            @RequestParam Date fechaDisponible,
            @RequestParam Integer precio,
            @RequestParam Integer anioFabricacion,
            @RequestParam String capacidad,
            @RequestParam(required = false) String mantenciones,
            @RequestParam(required = false) String condiciones,
            @RequestParam String mediosPago,
            Model model
    ) {
        try {
            MaquinariaDTO maquinariaDTO = MaquinariaDTO.builder()
                    .tipo(tipo)
                    .marca(marca)
                    .ubicacion(ubicacion)
                    .fechaDisponible(fechaDisponible)
                    .precio(precio)
                    .anioFabricacion(anioFabricacion)
                    .capacidad(capacidad)
                    .mantenciones(mantenciones)
                    .condiciones(condiciones)
                    .mediosPago(mediosPago)
                    .build();
            
            maquinariaService.registrarMaquinaria(maquinariaDTO);
            model.addAttribute("exito", "Maquinaria registrada exitosamente");
            return "redirect:/maquinaria/buscar";
        } catch (SecurityException e) {
            model.addAttribute("error", "Debe estar autenticado para registrar maquinaria");
            return "maquinaria_registrar";
        } catch (Exception e) {
            model.addAttribute("error", "Error al registrar maquinaria: " + e.getMessage());
            return "maquinaria_registrar";
        }
    }
}
