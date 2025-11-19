package com.ignabasti.agricola.service;

import com.ignabasti.agricola.dto.AvisoDTO;
import com.ignabasti.agricola.model.Aviso;
import com.ignabasti.agricola.model.Maquinaria;
import com.ignabasti.agricola.model.Usuario;
import com.ignabasti.agricola.repository.AvisoRepository;
import com.ignabasti.agricola.repository.MaquinariaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AvisoService {

    private final AvisoRepository avisoRepository;
    private final MaquinariaRepository maquinariaRepository;
    private final AuthenticationHelper authenticationHelper;

    @Transactional(readOnly = true)
    public List<AvisoDTO> obtenerTodosLosAvisos() {
        return avisoRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public AvisoDTO publicarAviso(AvisoDTO avisoDTO) {
        Usuario usuarioActual = authenticationHelper.obtenerUsuarioAutenticado();
        
        Maquinaria maquinaria = maquinariaRepository.findById(avisoDTO.getMaquinariaId())
                .orElseThrow(() -> new IllegalArgumentException("Maquinaria no encontrada con ID: " + avisoDTO.getMaquinariaId()));
        
        Aviso aviso = new Aviso();
        aviso.setUsuario(usuarioActual);
        aviso.setMaquinaria(maquinaria);
        aviso.setFecha_publicacion(new Date(System.currentTimeMillis()));
        aviso.setDestacado(avisoDTO.getDestacado() != null ? avisoDTO.getDestacado() : false);
        
        Aviso guardado = avisoRepository.save(aviso);
        log.info("Aviso publicado con ID: {} por usuario: {}", guardado.getId(), usuarioActual.getCorreo());
        
        return convertirADTO(guardado);
    }

    @Transactional
    public void eliminarAviso(Integer id) {
        Usuario usuarioActual = authenticationHelper.obtenerUsuarioAutenticado();
        
        Aviso aviso = avisoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Aviso no encontrado con ID: " + id));
        
        // Verificar que el usuario actual es el propietario
        if (!aviso.getUsuario().getId().equals(usuarioActual.getId())) {
            throw new SecurityException("No tiene permisos para eliminar este aviso");
        }
        
        avisoRepository.delete(aviso);
        log.info("Aviso eliminado con ID: {}", id);
    }

    private AvisoDTO convertirADTO(Aviso aviso) {
        return AvisoDTO.builder()
                .id(aviso.getId())
                .maquinariaId(aviso.getMaquinaria() != null ? aviso.getMaquinaria().getId() : null)
                .maquinariaTipo(aviso.getMaquinaria() != null ? aviso.getMaquinaria().getTipo() : null)
                .maquinariaMarca(aviso.getMaquinaria() != null ? aviso.getMaquinaria().getMarca() : null)
                .usuarioId(aviso.getUsuario() != null ? aviso.getUsuario().getId() : null)
                .usuarioNombre(aviso.getUsuario() != null ? aviso.getUsuario().getNombre() : null)
                .fechaPublicacion(aviso.getFecha_publicacion())
                .destacado(aviso.getDestacado())
                .build();
    }
}
