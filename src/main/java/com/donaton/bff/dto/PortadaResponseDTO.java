package com.donaton.bff.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class PortadaResponseDTO {
    private List<CausaDTO> causasActivas;
    private List<TopDonadorDTO> topDonadores;
    private ResumenDTO resumen;
    private String mensajeError;

    @Data
    @Builder
    public static class ResumenDTO {
        private BigDecimal totalRecaudado;
        private Long totalDonaciones;
        private Long causasActivas;
    }
}
