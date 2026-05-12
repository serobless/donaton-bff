package com.donaton.bff.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TransparenciaResponseDTO {
    private List<DonacionDTO> donaciones;
    private long totalRegistros;
    private String mensajeError;
}
