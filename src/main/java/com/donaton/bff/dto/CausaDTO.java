package com.donaton.bff.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CausaDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal metaMonto;
    private BigDecimal montoActual;
    private String estado;
    private String imagenUrl;
}
