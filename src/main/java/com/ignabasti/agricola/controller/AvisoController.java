package com.ignabasti.agricola.controller;

import com.ignabasti.agricola.model.Aviso;
import com.ignabasti.agricola.model.Maquinaria;
import com.ignabasti.agricola.model.Usuario;
import com.ignabasti.agricola.repository.AvisoRepository;
import com.ignabasti.agricola.repository.MaquinariaRepository;
import com.ignabasti.agricola.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Controller
public class AvisoController {
    @Autowired
    private AvisoRepository avisoRepository;
    @Autowired
    private MaquinariaRepository maquinariaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/avisos/nuevo")
    public String mostrarFormularioAviso(Authentication auth, Model model) {
        List<Maquinaria> maquinarias = maquinariaRepository.findAll();
        model.addAttribute("maquinarias", maquinarias);
        return "avisos";
    }

    @PostMapping("/avisos/nuevo")
    public String publicarAviso(Authentication auth,
                                @RequestParam Integer maquinariaId,
                                @RequestParam(required = false) Boolean destacado,
                                Model model) {
        String correo = auth.getName();
        Usuario usuario = usuarioRepository.findByCorreo(correo).orElse(null);
        Maquinaria maquinaria = maquinariaRepository.findById(maquinariaId).orElse(null);
        if (usuario != null && maquinaria != null) {
            Aviso aviso = new Aviso();
            aviso.setUsuario(usuario);
            aviso.setMaquinaria(maquinaria);
            aviso.setFecha_publicacion(new java.sql.Date(System.currentTimeMillis()));
            aviso.setDestacado(destacado != null ? destacado : false);
            avisoRepository.save(aviso);
            model.addAttribute("exito", "Aviso publicado correctamente.");
        }
        List<Maquinaria> maquinarias = maquinariaRepository.findAll();
        model.addAttribute("maquinarias", maquinarias);
        return "avisos";
    }
}
