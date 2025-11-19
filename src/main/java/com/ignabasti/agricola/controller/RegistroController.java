package com.ignabasti.agricola.controller;

import com.ignabasti.agricola.dto.UsuarioDTO;
import com.ignabasti.agricola.service.UsuarioService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class RegistroController {

    private final UsuarioService usuarioService;

    @GetMapping("/registro")
    public String registro() {
        return "registro";
    }

    @PostMapping("/registro")
    public String registrarUsuarioDesdeRegistro(@RequestParam String nombre,
                                                @RequestParam String correo,
                                                @RequestParam String contrasena,
                                                Model model) {
        try {
            if (usuarioService.existePorCorreo(correo)) {
                model.addAttribute("error", "El correo ya está registrado.");
                return "registro";
            }
            
            UsuarioDTO usuarioDTO = UsuarioDTO.builder()
                    .nombre(nombre)
                    .correo(correo)
                    .contrasena(contrasena)
                    .build();
            
            usuarioService.registrarUsuario(usuarioDTO);
            model.addAttribute("exito", "Usuario registrado correctamente. Ahora puedes iniciar sesión.");
            return "registro";
        } catch (Exception e) {
            model.addAttribute("error", "Error al registrar usuario: " + e.getMessage());
            return "registro";
        }
    }
}
