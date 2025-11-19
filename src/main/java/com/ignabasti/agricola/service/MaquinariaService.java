package com.ignabasti.agricola.service;

import com.ignabasti.agricola.dto.MaquinariaDTO;
import com.ignabasti.agricola.model.Maquinaria;
import com.ignabasti.agricola.model.Usuario;
import com.ignabasti.agricola.repository.MaquinariaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MaquinariaService {

    private final MaquinariaRepository maquinariaRepository;
    private final AuthenticationHelper authenticationHelper;

    @Transactional(readOnly = true)
    public List<MaquinariaDTO> obtenerTodasLasMaquinarias() {
        return maquinariaRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<MaquinariaDTO> obtenerMaquinariaPorId(Integer id) {
        return maquinariaRepository.findById(id)
                .map(this::convertirADTO);
    }

    @Transactional(readOnly = true)
    public List<MaquinariaDTO> buscarMaquinarias(String tipo, String ubicacion, String fecha, Integer precioMaximo) {
        List<Maquinaria> maquinarias = maquinariaRepository.findAll();
        
        // Aplicar filtros
        if (tipo != null && !tipo.isEmpty()) {
            maquinarias = maquinarias.stream()
                    .filter(m -> m.getTipo().equalsIgnoreCase(tipo))
                    .collect(Collectors.toList());
        }
        
        if (ubicacion != null && !ubicacion.isEmpty()) {
            maquinarias = maquinarias.stream()
                    .filter(m -> m.getUbicacion() != null && m.getUbicacion().toLowerCase().contains(ubicacion.toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        if (fecha != null && !fecha.isEmpty()) {
            maquinarias = maquinarias.stream()
                    .filter(m -> m.getFecha_disponible() != null && m.getFecha_disponible().toString().equals(fecha))
                    .collect(Collectors.toList());
        }
        
        if (precioMaximo != null) {
            maquinarias = maquinarias.stream()
                    .filter(m -> m.getPrecio() != null && m.getPrecio() <= precioMaximo)
                    .collect(Collectors.toList());
        }
        
        return maquinarias.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public MaquinariaDTO registrarMaquinaria(MaquinariaDTO maquinariaDTO) {
        Usuario usuarioActual = authenticationHelper.obtenerUsuarioAutenticado();
        
        Maquinaria maquinaria = new Maquinaria();
        maquinaria.setTipo(maquinariaDTO.getTipo());
        maquinaria.setMarca(maquinariaDTO.getMarca());
        maquinaria.setUbicacion(maquinariaDTO.getUbicacion());
        maquinaria.setFecha_disponible(maquinariaDTO.getFechaDisponible());
        maquinaria.setPrecio(maquinariaDTO.getPrecio());
        maquinaria.setAnio_fabricacion(maquinariaDTO.getAnioFabricacion());
        maquinaria.setCapacidad(maquinariaDTO.getCapacidad());
        maquinaria.setMantenciones(maquinariaDTO.getMantenciones());
        maquinaria.setCondiciones(maquinariaDTO.getCondiciones());
        maquinaria.setMedios_pago(maquinariaDTO.getMediosPago());
        maquinaria.setUsuario(usuarioActual);
        
        Maquinaria guardada = maquinariaRepository.save(maquinaria);
        log.info("Maquinaria registrada con ID: {} por usuario: {}", guardada.getId(), usuarioActual.getCorreo());
        
        return convertirADTO(guardada);
    }

    @Transactional
    public MaquinariaDTO actualizarMaquinaria(Integer id, MaquinariaDTO maquinariaDTO) {
        Usuario usuarioActual = authenticationHelper.obtenerUsuarioAutenticado();
        
        Maquinaria maquinaria = maquinariaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Maquinaria no encontrada con ID: " + id));
        
        // Verificar que el usuario actual es el propietario
        if (!maquinaria.getUsuario().getId().equals(usuarioActual.getId())) {
            throw new SecurityException("No tiene permisos para modificar esta maquinaria");
        }
        
        maquinaria.setTipo(maquinariaDTO.getTipo());
        maquinaria.setMarca(maquinariaDTO.getMarca());
        maquinaria.setUbicacion(maquinariaDTO.getUbicacion());
        maquinaria.setFecha_disponible(maquinariaDTO.getFechaDisponible());
        maquinaria.setPrecio(maquinariaDTO.getPrecio());
        maquinaria.setAnio_fabricacion(maquinariaDTO.getAnioFabricacion());
        maquinaria.setCapacidad(maquinariaDTO.getCapacidad());
        maquinaria.setMantenciones(maquinariaDTO.getMantenciones());
        maquinaria.setCondiciones(maquinariaDTO.getCondiciones());
        maquinaria.setMedios_pago(maquinariaDTO.getMediosPago());
        
        Maquinaria actualizada = maquinariaRepository.save(maquinaria);
        log.info("Maquinaria actualizada con ID: {}", actualizada.getId());
        
        return convertirADTO(actualizada);
    }

    @Transactional
    public void eliminarMaquinaria(Integer id) {
        Usuario usuarioActual = authenticationHelper.obtenerUsuarioAutenticado();
        
        Maquinaria maquinaria = maquinariaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Maquinaria no encontrada con ID: " + id));
        
        // Verificar que el usuario actual es el propietario
        if (!maquinaria.getUsuario().getId().equals(usuarioActual.getId())) {
            throw new SecurityException("No tiene permisos para eliminar esta maquinaria");
        }
        
        maquinariaRepository.delete(maquinaria);
        log.info("Maquinaria eliminada con ID: {}", id);
    }

    private MaquinariaDTO convertirADTO(Maquinaria maquinaria) {
        return MaquinariaDTO.builder()
                .id(maquinaria.getId())
                .tipo(maquinaria.getTipo())
                .marca(maquinaria.getMarca())
                .ubicacion(maquinaria.getUbicacion())
                .fechaDisponible(maquinaria.getFecha_disponible())
                .precio(maquinaria.getPrecio())
                .anioFabricacion(maquinaria.getAnio_fabricacion())
                .capacidad(maquinaria.getCapacidad())
                .mantenciones(maquinaria.getMantenciones())
                .condiciones(maquinaria.getCondiciones())
                .mediosPago(maquinaria.getMedios_pago())
                .usuarioId(maquinaria.getUsuario() != null ? maquinaria.getUsuario().getId() : null)
                .usuarioNombre(maquinaria.getUsuario() != null ? maquinaria.getUsuario().getNombre() : null)
                .build();
    }
}
