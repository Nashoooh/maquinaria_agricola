package com.ignabasti.agricola.service;

import com.ignabasti.agricola.dto.ReservaDTO;
import com.ignabasti.agricola.model.Maquinaria;
import com.ignabasti.agricola.model.Reserva;
import com.ignabasti.agricola.model.Usuario;
import com.ignabasti.agricola.repository.MaquinariaRepository;
import com.ignabasti.agricola.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final MaquinariaRepository maquinariaRepository;
    private final AuthenticationHelper authenticationHelper;

    @Transactional(readOnly = true)
    public List<ReservaDTO> obtenerTodasLasReservas() {
        return reservaRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReservaDTO> obtenerReservasDelUsuarioActual() {
        Usuario usuarioActual = authenticationHelper.obtenerUsuarioAutenticado();
        return reservaRepository.findAll().stream()
                .filter(r -> r.getUsuario().getId().equals(usuarioActual.getId()))
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReservaDTO crearReserva(ReservaDTO reservaDTO) {
        Usuario usuarioActual = authenticationHelper.obtenerUsuarioAutenticado();
        
        Maquinaria maquinaria = maquinariaRepository.findById(reservaDTO.getMaquinariaId())
                .orElseThrow(() -> new IllegalArgumentException("Maquinaria no encontrada con ID: " + reservaDTO.getMaquinariaId()));
        
        Reserva reserva = new Reserva();
        reserva.setUsuario(usuarioActual);
        reserva.setMaquinaria(maquinaria);
        reserva.setFecha_reserva(reservaDTO.getFechaReserva());
        
        Reserva guardada = reservaRepository.save(reserva);
        log.info("Reserva creada con ID: {} por usuario: {}", guardada.getId(), usuarioActual.getCorreo());
        
        return convertirADTO(guardada);
    }

    @Transactional
    public void eliminarReserva(Integer id) {
        Usuario usuarioActual = authenticationHelper.obtenerUsuarioAutenticado();
        
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada con ID: " + id));
        
        // Verificar que el usuario actual es el propietario
        if (!reserva.getUsuario().getId().equals(usuarioActual.getId())) {
            throw new SecurityException("No tiene permisos para eliminar esta reserva");
        }
        
        reservaRepository.delete(reserva);
        log.info("Reserva eliminada con ID: {}", id);
    }

    private ReservaDTO convertirADTO(Reserva reserva) {
        return ReservaDTO.builder()
                .id(reserva.getId())
                .maquinariaId(reserva.getMaquinaria() != null ? reserva.getMaquinaria().getId() : null)
                .maquinariaTipo(reserva.getMaquinaria() != null ? reserva.getMaquinaria().getTipo() : null)
                .maquinariaMarca(reserva.getMaquinaria() != null ? reserva.getMaquinaria().getMarca() : null)
                .usuarioId(reserva.getUsuario() != null ? reserva.getUsuario().getId() : null)
                .usuarioNombre(reserva.getUsuario() != null ? reserva.getUsuario().getNombre() : null)
                .fechaReserva(reserva.getFecha_reserva())
                .build();
    }
}
