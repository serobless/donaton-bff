package com.donaton.bff.controller;

import com.donaton.bff.client.DonacionesClient;
import com.donaton.bff.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/bff")
@RequiredArgsConstructor
public class BffController {

    private final DonacionesClient donacionesClient;

    /**
     * GET /bff/portada
     * Agrega: causas activas + top donadores + resumen general.
     * Endpoint público, no requiere autenticación.
     */
    @GetMapping("/portada")
    public Mono<ResponseEntity<PortadaResponseDTO>> getPortada() {
        log.info("GET /bff/portada");

        Mono<List<CausaDTO>> causasMono = donacionesClient.getCausasActivas();
        Mono<List<TopDonadorDTO>> topMono = donacionesClient.getTopDonadores(5);
        Mono<BigDecimal> totalMono = donacionesClient.getTotalRecaudado();
        Mono<Long> conteoMono = donacionesClient.getConteoDonaciones();

        return Mono.zip(causasMono, topMono, totalMono, conteoMono)
                .map(tuple -> {
                    List<CausaDTO> causas = tuple.getT1();
                    List<TopDonadorDTO> top = tuple.getT2();
                    BigDecimal total = tuple.getT3();
                    Long conteo = tuple.getT4();

                    PortadaResponseDTO response = PortadaResponseDTO.builder()
                            .causasActivas(causas)
                            .topDonadores(top)
                            .resumen(PortadaResponseDTO.ResumenDTO.builder()
                                    .totalRecaudado(total)
                                    .totalDonaciones(conteo)
                                    .causasActivas((long) causas.size())
                                    .build())
                            .build();

                    // Si todos los datos están vacíos, el CB abrió: informar al cliente
                    if (causas.isEmpty() && top.isEmpty() && total.compareTo(BigDecimal.ZERO) == 0) {
                        response.setMensajeError(
                                "El servicio de donaciones no está disponible en este momento. Intente más tarde.");
                    }

                    return ResponseEntity.ok(response);
                });
    }

    /**
     * GET /bff/dashboard
     * Agrega: stats totales + top donadores + últimas donaciones para el admin.
     */
    @GetMapping("/dashboard")
    public Mono<ResponseEntity<DashboardResponseDTO>> getDashboard() {
        log.info("GET /bff/dashboard");

        Mono<BigDecimal> totalMono = donacionesClient.getTotalRecaudado();
        Mono<Long> conteoMono = donacionesClient.getConteoDonaciones();
        Mono<List<TopDonadorDTO>> topMono = donacionesClient.getTopDonadores(10);
        Mono<List<DonacionDTO>> ultimasMono = donacionesClient.getUltimasDonaciones(10);
        Mono<List<CausaDTO>> causasMono = donacionesClient.getCausasActivas();
        Mono<Long> testimoniosMono = donacionesClient.getTestimoniosPendientes();

        return Mono.zip(totalMono, conteoMono, topMono, ultimasMono, causasMono, testimoniosMono)
                .map(tuple -> {
                    BigDecimal total = tuple.getT1();
                    Long conteo = tuple.getT2();
                    List<TopDonadorDTO> top = tuple.getT3();
                    List<DonacionDTO> ultimas = tuple.getT4();
                    List<CausaDTO> causas = tuple.getT5();
                    Long testimonios = tuple.getT6();

                    boolean sinDatos = top.isEmpty() && ultimas.isEmpty()
                            && total.compareTo(BigDecimal.ZERO) == 0;

                    DashboardResponseDTO response = DashboardResponseDTO.builder()
                            .totalDonado(total)
                            .totalDonaciones(conteo)
                            .causasActivas((long) causas.size())
                            .causasInactivas(0L)
                            .topDonadores(top)
                            .ultimasDonaciones(ultimas)
                            .testimoniosPendientes(testimonios)
                            .mensajeError(sinDatos
                                    ? "El servicio de donaciones no está disponible en este momento."
                                    : null)
                            .build();

                    return ResponseEntity.ok(response);
                });
    }

    /**
     * GET /bff/transparencia
     * Devuelve la tabla pública de todas las donaciones auditadas.
     */
    @GetMapping("/transparencia")
    public Mono<ResponseEntity<TransparenciaResponseDTO>> getTransparencia() {
        log.info("GET /bff/transparencia");

        return donacionesClient.getDonaciones()
                .map(donaciones -> {
                    TransparenciaResponseDTO response = TransparenciaResponseDTO.builder()
                            .donaciones(donaciones)
                            .totalRegistros(donaciones.size())
                            .mensajeError(donaciones.isEmpty()
                                    ? "No se pudieron cargar los datos de transparencia en este momento."
                                    : null)
                            .build();

                    return ResponseEntity.ok(response);
                });
    }
}
