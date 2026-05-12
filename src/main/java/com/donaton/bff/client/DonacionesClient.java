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
        log.debug("Llamando a ms-donaciones: GET /causas?estado=ACTIVA");
        return webClient.get()
                .uri("/causas?estado=ACTIVA")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<CausaDTO>>() {})
                .doOnError(e -> log.warn("Error al obtener causas activas: {}", e.getMessage()));
    }

    public Mono<List<CausaDTO>> causasActivasFallback(Throwable t) {
        log.warn("Circuit Breaker activo para causasActivas: {}", t.getMessage());
        return Mono.just(List.of());
    }

    // --- Top Donadores ---

    @CircuitBreaker(name = CB_NAME, fallbackMethod = "topDonadoresFallback")
    public Mono<List<TopDonadorDTO>> getTopDonadores(int limit) {
        log.debug("Llamando a ms-donaciones: GET /donaciones/top?limit={}", limit);
        return webClient.get()
                .uri("/donaciones/top?limit={limit}", limit)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<TopDonadorDTO>>() {})
                .doOnError(e -> log.warn("Error al obtener top donadores: {}", e.getMessage()));
    }

    public Mono<List<TopDonadorDTO>> topDonadoresFallback(int limit, Throwable t) {
        log.warn("Circuit Breaker activo para topDonadores: {}", t.getMessage());
        return Mono.just(List.of());
    }

    // --- Total recaudado ---

    @CircuitBreaker(name = CB_NAME, fallbackMethod = "totalRecaudadoFallback")
    public Mono<BigDecimal> getTotalRecaudado() {
        log.debug("Llamando a ms-donaciones: GET /donaciones/total");
        return webClient.get()
                .uri("/donaciones/total")
                .retrieve()
                .bodyToMono(BigDecimal.class)
                .doOnError(e -> log.warn("Error al obtener total recaudado: {}", e.getMessage()));
    }

    public Mono<BigDecimal> totalRecaudadoFallback(Throwable t) {
        log.warn("Circuit Breaker activo para totalRecaudado: {}", t.getMessage());
        return Mono.just(BigDecimal.ZERO);
    }

    // --- Conteo de donaciones ---

    @CircuitBreaker(name = CB_NAME, fallbackMethod = "conteoFallback")
    public Mono<Long> getConteoDonaciones() {
        log.debug("Llamando a ms-donaciones: GET /donaciones/count");
        return webClient.get()
                .uri("/donaciones/count")
                .retrieve()
                .bodyToMono(Long.class)
                .doOnError(e -> log.warn("Error al obtener conteo de donaciones: {}", e.getMessage()));
    }

    public Mono<Long> conteoFallback(Throwable t) {
        log.warn("Circuit Breaker activo para conteoDonaciones: {}", t.getMessage());
        return Mono.just(0L);
    }

    // --- Todas las donaciones (transparencia) ---

    @CircuitBreaker(name = CB_NAME, fallbackMethod = "donacionesFallback")
    public Mono<List<DonacionDTO>> getDonaciones() {
        log.debug("Llamando a ms-donaciones: GET /donaciones");
        return webClient.get()
                .uri("/donaciones")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<DonacionDTO>>() {})
                .doOnError(e -> log.warn("Error al obtener donaciones: {}", e.getMessage()));
    }

    public Mono<List<DonacionDTO>> donacionesFallback(Throwable t) {
        log.warn("Circuit Breaker activo para donaciones: {}", t.getMessage());
        return Mono.just(List.of());
    }

    // --- Últimas donaciones (dashboard) ---

    @CircuitBreaker(name = CB_NAME, fallbackMethod = "ultimasDonacionesFallback")
    public Mono<List<DonacionDTO>> getUltimasDonaciones(int limit) {
        log.debug("Llamando a ms-donaciones: GET /donaciones/ultimas?limit={}", limit);
        return webClient.get()
                .uri("/donaciones/ultimas?limit={limit}", limit)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<DonacionDTO>>() {})
                .doOnError(e -> log.warn("Error al obtener últimas donaciones: {}", e.getMessage()));
    }

    public Mono<List<DonacionDTO>> ultimasDonacionesFallback(int limit, Throwable t) {
        log.warn("Circuit Breaker activo para ultimasDonaciones: {}", t.getMessage());
        return Mono.just(List.of());
    }
}
