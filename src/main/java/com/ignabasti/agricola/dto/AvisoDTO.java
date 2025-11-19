package com.ignabasti.agricola.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvisoDTO {
    
    private Integer id;
    
    @NotNull(message = "El ID de la maquinaria es obligatorio")
    private Integer maquinariaId;
    
    private String maquinariaTipo;
    private String maquinariaMarca;
    
    private Integer usuarioId;
    private String usuarioNombre;
    
    private Date fechaPublicacion;
    
    @Builder.Default
    private Boolean destacado = false;
}
