package com.donaton.bff.client;

import com.donaton.bff.dto.CausaDTO;
import com.donaton.bff.dto.DonacionDTO;
import com.donaton.bff.dto.TopDonadorDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
public class DonacionesClient {

    private static final String CB_NAME = "donaciones-cb";

    private final WebClient webClient;

    public DonacionesClient(@Qualifier("donacionesWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    // --- Causas ---

    @CircuitBreaker(name = CB_NAME, fallbackMethod = "causasActivasFallback")
    public Mono<List<CausaDTO>> getCausasActivas() {
        log.debug("Llamando a ms-donaciones: GET /api/causas");
        return webClient.get()
                .uri("/api/causas")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<CausaDTO>>() {})
                .doOnError(e -> log.error("Error al obtener causas activas [{}: {}]",
                        e.getClass().getSimpleName(), e.getMessage()));
    }

    public Mono<List<CausaDTO>> causasActivasFallback(Throwable t) {
        log.error("Fallback causasActivas activado [{}: {}]", t.getClass().getSimpleName(), t.getMessage());
        return Mono.just(List.of());
    }

    // --- Top Donadores ---

    @CircuitBreaker(name = CB_NAME, fallbackMethod = "topDonadoresFallback")
    public Mono<List<TopDonadorDTO>> getTopDonadores(int limit) {
        log.debug("Llamando a ms-donaciones: GET /api/donaciones/top?limit={}", limit);
        return webClient.get()
                .uri("/api/donaciones/top?limit={limit}", limit)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<TopDonadorDTO>>() {})
                .doOnError(e -> log.error("Error al obtener top donadores [{}: {}]",
                        e.getClass().getSimpleName(), e.getMessage()));
    }

    public Mono<List<TopDonadorDTO>> topDonadoresFallback(int limit, Throwable t) {
        log.error("Fallback topDonadores activado [{}: {}]", t.getClass().getSimpleName(), t.getMessage());
        return Mono.just(List.of());
    }

    // --- Total recaudado ---

    @CircuitBreaker(name = CB_NAME, fallbackMethod = "totalRecaudadoFallback")
    public Mono<BigDecimal> getTotalRecaudado() {
        log.debug("Llamando a ms-donaciones: GET /api/donaciones/total");
        return webClient.get()
                .uri("/api/donaciones/total")
                .retrieve()
                .bodyToMono(BigDecimal.class)
                .doOnError(e -> log.error("Error al obtener total recaudado [{}: {}]",
                        e.getClass().getSimpleName(), e.getMessage()));
    }

    public Mono<BigDecimal> totalRecaudadoFallback(Throwable t) {
        log.error("Fallback totalRecaudado activado [{}: {}]", t.getClass().getSimpleName(), t.getMessage());
        return Mono.just(BigDecimal.ZERO);
    }

    // --- Conteo de donaciones ---

    @CircuitBreaker(name = CB_NAME, fallbackMethod = "conteoFallback")
    public Mono<Long> getConteoDonaciones() {
        log.debug("Llamando a ms-donaciones: GET /api/donaciones/count");
        return webClient.get()
                .uri("/api/donaciones/count")
                .retrieve()
                .bodyToMono(Long.class)
                .doOnError(e -> log.error("Error al obtener conteo de donaciones [{}: {}]",
                        e.getClass().getSimpleName(), e.getMessage()));
    }

    public Mono<Long> conteoFallback(Throwable t) {
        log.error("Fallback conteoDonaciones activado [{}: {}]", t.getClass().getSimpleName(), t.getMessage());
        return Mono.just(0L);
    }

    // --- Todas las donaciones (transparencia) ---

    @CircuitBreaker(name = CB_NAME, fallbackMethod = "donacionesFallback")
    public Mono<List<DonacionDTO>> getDonaciones() {
        log.debug("Llamando a ms-donaciones: GET /api/donaciones/transparencia");
        return webClient.get()
                .uri("/api/donaciones/transparencia")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<DonacionDTO>>() {})
                .doOnError(e -> log.error("Error al obtener donaciones [{}: {}]",
                        e.getClass().getSimpleName(), e.getMessage()));
    }

    public Mono<List<DonacionDTO>> donacionesFallback(Throwable t) {
        log.error("Fallback donaciones activado [{}: {}]", t.getClass().getSimpleName(), t.getMessage());
        return Mono.just(List.of());
    }

    // --- Últimas donaciones (dashboard) ---

    @CircuitBreaker(name = CB_NAME, fallbackMethod = "ultimasDonacionesFallback")
    public Mono<List<DonacionDTO>> getUltimasDonaciones(int limit) {
        log.debug("Llamando a ms-donaciones: GET /api/donaciones/ultimas?limit={}", limit);
        return webClient.get()
                .uri("/api/donaciones/ultimas?limit={limit}", limit)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<DonacionDTO>>() {})
                .doOnError(e -> log.error("Error al obtener últimas donaciones [{}: {}]",
                        e.getClass().getSimpleName(), e.getMessage()));
    }

    public Mono<List<DonacionDTO>> ultimasDonacionesFallback(int limit, Throwable t) {
        log.error("Fallback ultimasDonaciones activado [{}: {}]", t.getClass().getSimpleName(), t.getMessage());
        return Mono.just(List.of());
    }
}
