package com.ignabasti.agricola.controller;

import com.ignabasti.agricola.model.Usuario;
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
        if (usuarioService.existePorCorreo(correo)) {
            model.addAttribute("error", "El correo ya está registrado.");
            return "registro";
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setCorreo(correo);
        usuario.setContrasena(contrasena);
        usuarioService.registrarUsuario(usuario);
        model.addAttribute("exito", "Usuario registrado correctamente. Ahora puedes iniciar sesión.");
        return "registro";
    }
}
