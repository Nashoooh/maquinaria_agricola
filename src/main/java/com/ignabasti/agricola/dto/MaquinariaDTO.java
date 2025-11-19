package com.ignabasti.agricola.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaquinariaDTO {
    
    private Integer id;
    
    @NotBlank(message = "El tipo es obligatorio")
    private String tipo;
    
    @NotBlank(message = "La marca es obligatoria")
    private String marca;
    
    @NotBlank(message = "La ubicación es obligatoria")
    private String ubicacion;
    
    @NotNull(message = "La fecha disponible es obligatoria")
    private Date fechaDisponible;
    
    @NotNull(message = "El precio es obligatorio")
    @Min(value = 0, message = "El precio debe ser mayor o igual a 0")
    private Integer precio;
    
    @NotNull(message = "El año de fabricación es obligatorio")
    @Min(value = 1900, message = "El año debe ser mayor a 1900")
    @Max(value = 2100, message = "El año no puede ser mayor a 2100")
    private Integer anioFabricacion;
    
    @NotBlank(message = "La capacidad es obligatoria")
    private String capacidad;
    
    private String mantenciones;
    
    private String condiciones;
    
    @NotBlank(message = "Los medios de pago son obligatorios")
    private String mediosPago;
    
    private Integer usuarioId;
    private String usuarioNombre;
}
