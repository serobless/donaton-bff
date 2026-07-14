package com.donaton.bff.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CausaDTO {
    private Long id;
    private String titulo;
    private String descripcion;
    private BigDecimal meta;
    private BigDecimal recaudado;
    private Boolean activa;
    private String categoria;
    private String imagenUrl;
    private Integer diasRestantes;
    private Boolean destacada;
    private String urgencia;
    private LocalDate fechaFin;
}
