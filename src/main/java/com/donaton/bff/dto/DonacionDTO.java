package com.donaton.bff.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DonacionDTO {
    private Long id;
    private String donadorNombre;
    private String causaNombre;
    private BigDecimal monto;
    private LocalDateTime fecha;
    private String estado;
    private Boolean esEmpresa;
    private String nombreEmpresa;
    private Boolean requiereAprobacion;
}
