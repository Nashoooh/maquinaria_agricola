package com.ignabasti.agricola.controller;

import com.ignabasti.agricola.dto.MaquinariaDTO;
import com.ignabasti.agricola.dto.ReservaDTO;
import com.ignabasti.agricola.service.MaquinariaService;
import com.ignabasti.agricola.service.ReservaService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Date;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ReservaController {

    private final MaquinariaService maquinariaService;
    private final ReservaService reservaService;

    @GetMapping("/maquinaria/reserva")
    public String mostrarFormularioReserva(Model model) {
        List<MaquinariaDTO> maquinarias = maquinariaService.obtenerTodasLasMaquinarias();
        model.addAttribute("maquinarias", maquinarias);
        return "maquinaria_reserva";
    }

    @PostMapping("/maquinaria/reserva")
    public String reservarMaquinaria(@RequestParam Integer maquinariaId,
                                    @RequestParam String fechaReserva,
                                    Model model) {
        try {
            ReservaDTO reservaDTO = ReservaDTO.builder()
                    .maquinariaId(maquinariaId)
                    .fechaReserva(Date.valueOf(fechaReserva))
                    .build();
            
            reservaService.crearReserva(reservaDTO);
            model.addAttribute("exito", "Reserva realizada correctamente.");
        } catch (SecurityException e) {
            model.addAttribute("error", "Debe estar autenticado para realizar una reserva.");
        } catch (Exception e) {
            model.addAttribute("error", "Error al realizar reserva: " + e.getMessage());
        }
        
        List<MaquinariaDTO> maquinarias = maquinariaService.obtenerTodasLasMaquinarias();
        model.addAttribute("maquinarias", maquinarias);
        return "maquinaria_reserva";
    }
}
