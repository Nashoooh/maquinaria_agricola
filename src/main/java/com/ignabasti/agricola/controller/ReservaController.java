package com.ignabasti.agricola.controller;

import com.ignabasti.agricola.model.Maquinaria;
import com.ignabasti.agricola.model.Reserva;
import com.ignabasti.agricola.model.Usuario;
import com.ignabasti.agricola.repository.MaquinariaRepository;
import com.ignabasti.agricola.repository.ReservaRepository;
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
public class ReservaController {
    @Autowired
    private MaquinariaRepository maquinariaRepository;
    @Autowired
    private ReservaRepository reservaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/maquinaria/reserva")
    public String mostrarFormularioReserva(Model model) {
        List<Maquinaria> maquinarias = maquinariaRepository.findAll();
        model.addAttribute("maquinarias", maquinarias);
        return "maquinaria_reserva";
    }

    @PostMapping("/maquinaria/reserva")
    public String reservarMaquinaria(Authentication auth,
                                    @RequestParam Integer maquinariaId,
                                    @RequestParam String fecha_reserva,
                                    Model model) {
        String correo = auth.getName();
        Usuario usuario = usuarioRepository.findByCorreo(correo).orElse(null);
        Maquinaria maquinaria = maquinariaRepository.findById(maquinariaId).orElse(null);
        if (usuario != null && maquinaria != null) {
            Reserva reserva = new Reserva();
            reserva.setUsuario(usuario);
            reserva.setMaquinaria(maquinaria);
            reserva.setFecha_reserva(java.sql.Date.valueOf(fecha_reserva));
            reservaRepository.save(reserva);
            model.addAttribute("exito", "Reserva realizada correctamente.");
        }
        List<Maquinaria> maquinarias = maquinariaRepository.findAll();
        model.addAttribute("maquinarias", maquinarias);
        return "maquinaria_reserva";
    }
}
