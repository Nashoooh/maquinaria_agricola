package com.ignabasti.agricola.controller;

import com.ignabasti.agricola.dto.UsuarioDTO;
import com.ignabasti.agricola.service.AuthenticationHelper;
import com.ignabasti.agricola.service.UsuarioService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class PerfilController {

    private final UsuarioService usuarioService;
    private final AuthenticationHelper authenticationHelper;

    @GetMapping("/perfil")
    public String mostrarPerfil(Model model) {
        try {
            UsuarioDTO usuario = usuarioService.obtenerUsuarioPorCorreo(
                    authenticationHelper.getAuthenticatedUserEmail());
            model.addAttribute("usuario", usuario);
            return "perfil";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar perfil");
            return "redirect:/login";
        }
    }

    @PostMapping("/perfil")
    public String actualizarPerfil(@RequestParam String direccion,
                                  @RequestParam String telefono,
                                  @RequestParam String cultivos,
                                  Model model) {
        try {
            String correo = authenticationHelper.getAuthenticatedUserEmail();
            UsuarioDTO usuarioDTO = usuarioService.obtenerUsuarioPorCorreo(correo);
            
            usuarioDTO.setDireccion(direccion);
            usuarioDTO.setTelefono(telefono);
            usuarioDTO.setCultivos(cultivos);
            
            usuarioService.actualizarUsuario(correo, usuarioDTO);
            model.addAttribute("exito", "Perfil actualizado correctamente.");
            model.addAttribute("usuario", usuarioDTO);
            return "perfil";
        } catch (Exception e) {
            model.addAttribute("error", "Error al actualizar perfil");
            return "perfil";
        }
    }
}
