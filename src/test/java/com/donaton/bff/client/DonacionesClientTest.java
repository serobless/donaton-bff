package com.donaton.bff.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifica los métodos fallback del Circuit Breaker.
 * En producción, Resilience4j AOP llama a estos métodos cuando el servicio falla.
 * Aquí los invocamos directamente para testear su contrato de respuesta.
 */
@DisplayName("DonacionesClient — Circuit Breaker fallbacks")
class DonacionesClientTest {

    private DonacionesClient client;

    @BeforeEach
    void setUp() {
        // WebClient null: solo probamos los fallbacks, que no invocan WebClient
        client = new DonacionesClient(null);
    }

    @Test
    @DisplayName("circuitBreakerSeActivaConFallos: fallback de causas retorna lista vacía ante error de WebClient")
    void circuitBreakerSeActivaConFallos() {
        RuntimeException error =
                new RuntimeException("Connection refused: ms-donaciones no disponible");

        StepVerifier.create(client.causasActivasFallback(error))
                .assertNext(lista -> assertThat(lista).isEmpty())
                .verifyComplete();
    }

    @Test
    @DisplayName("topDonadoresFallback retorna lista vacía ante timeout del servicio")
    void topDonadoresFallbackRetornaListaVacia() {
        RuntimeException error = new RuntimeException("Read timed out");

        StepVerifier.create(client.topDonadoresFallback(5, error))
                .assertNext(lista -> assertThat(lista).isEmpty())
                .verifyComplete();
    }

    @Test
    @DisplayName("totalRecaudadoFallback retorna BigDecimal.ZERO ante circuit breaker abierto")
    void totalRecaudadoFallbackRetornaCero() {
        RuntimeException error = new RuntimeException("Circuit Breaker OPEN");

        StepVerifier.create(client.totalRecaudadoFallback(error))
                .assertNext(total ->
                        assertThat(total).isEqualByComparingTo(BigDecimal.ZERO))
                .verifyComplete();
    }

    @Test
    @DisplayName("conteoFallback retorna 0L ante fallo del servicio")
    void conteoFallbackRetornaCero() {
        RuntimeException error = new RuntimeException("Service unavailable");

        StepVerifier.create(client.conteoFallback(error))
                .assertNext(conteo -> assertThat(conteo).isZero())
                .verifyComplete();
    }

    @Test
    @DisplayName("donacionesFallback retorna lista vacía ante fallo del servicio")
    void donacionesFallbackRetornaListaVacia() {
        RuntimeException error = new RuntimeException("Internal Server Error");

        StepVerifier.create(client.donacionesFallback(error))
                .assertNext(lista -> assertThat(lista).isEmpty())
                .verifyComplete();
    }
}
