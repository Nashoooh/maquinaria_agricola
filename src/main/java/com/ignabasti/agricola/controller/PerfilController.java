package com.ignabasti.agricola.controller;

import com.ignabasti.agricola.model.Usuario;
import com.ignabasti.agricola.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class PerfilController {

    private final UsuarioRepository usuarioRepository;

    @GetMapping("/perfil")
    public String mostrarPerfil(Authentication auth, Model model) {
        String correo = auth.getName();
        Usuario usuario = usuarioRepository.findByCorreo(correo).orElse(null);
        model.addAttribute("usuario", usuario);
        return "perfil";
    }

    @PostMapping("/perfil")
    public String actualizarPerfil(Authentication auth,
                                  @RequestParam String direccion,
                                  @RequestParam String telefono,
                                  @RequestParam String cultivos,
                                  Model model) {
        String correo = auth.getName();
        Usuario usuario = usuarioRepository.findByCorreo(correo).orElse(null);
        if (usuario != null) {
            usuario.setDireccion(direccion);
            usuario.setTelefono(telefono);
            usuario.setCultivos(cultivos);
            usuarioRepository.save(usuario);
            model.addAttribute("exito", "Perfil actualizado correctamente.");
        }
        model.addAttribute("usuario", usuario);
        return "perfil";
    }
}
