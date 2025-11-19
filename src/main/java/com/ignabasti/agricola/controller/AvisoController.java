package com.ignabasti.agricola.controller;

import com.ignabasti.agricola.dto.AvisoDTO;
import com.ignabasti.agricola.dto.MaquinariaDTO;
import com.ignabasti.agricola.service.AvisoService;
import com.ignabasti.agricola.service.MaquinariaService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AvisoController {
    
    private final AvisoService avisoService;
    private final MaquinariaService maquinariaService;

    @GetMapping("/maquinaria/avisos")
    public String mostrarFormularioAviso(Model model) {
        List<MaquinariaDTO> maquinarias = maquinariaService.obtenerTodasLasMaquinarias();
        model.addAttribute("maquinarias", maquinarias);
        return "maquinaria_avisos";
    }

    @PostMapping("/maquinaria/avisos")
    public String publicarAviso(@RequestParam Integer maquinariaId,
                                @RequestParam(required = false) Boolean destacado,
                                Model model) {
        try {
            AvisoDTO avisoDTO = AvisoDTO.builder()
                    .maquinariaId(maquinariaId)
                    .destacado(destacado != null ? destacado : false)
                    .build();
            
            avisoService.publicarAviso(avisoDTO);
            model.addAttribute("exito", "Aviso publicado correctamente.");
        } catch (SecurityException e) {
            model.addAttribute("error", "Debe estar autenticado para publicar un aviso.");
        } catch (Exception e) {
            model.addAttribute("error", "Error al publicar aviso: " + e.getMessage());
        }
        
        List<MaquinariaDTO> maquinarias = maquinariaService.obtenerTodasLasMaquinarias();
        model.addAttribute("maquinarias", maquinarias);
        return "maquinaria_avisos";
    }
}
