package com.donaton.bff.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class DashboardResponseDTO {
    private BigDecimal totalDonado;
    private Long totalDonaciones;
    private Long causasActivas;
    private Long causasInactivas;
    private List<TopDonadorDTO> topDonadores;
    private List<DonacionDTO> ultimasDonaciones;
    private Long testimoniosPendientes;
    private String mensajeError;
}
