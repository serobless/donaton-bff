package com.donaton.bff.controller;

import com.donaton.bff.client.DonacionesClient;
import com.donaton.bff.dto.CausaDTO;
import com.donaton.bff.dto.PortadaResponseDTO;
import com.donaton.bff.dto.TopDonadorDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@WebFluxTest(BffController.class)
class BffControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private DonacionesClient donacionesClient;

    @Test
    @DisplayName("portadaRetornaDatosReales: ms-donaciones responde → BFF retorna causas y top donadores")
    void portadaRetornaDatosReales() {
        // given
        CausaDTO causa = new CausaDTO();
        causa.setId(1L);
        causa.setTitulo("Campaña Solidaria");
        causa.setActiva(true);
        causa.setMeta(BigDecimal.valueOf(100_000));
        causa.setRecaudado(BigDecimal.valueOf(45_000));

        TopDonadorDTO donador = new TopDonadorDTO("María López", BigDecimal.valueOf(20_000), 5L);

        when(donacionesClient.getCausasActivas())
                .thenReturn(Mono.just(List.of(causa)));
        when(donacionesClient.getTopDonadores(anyInt()))
                .thenReturn(Mono.just(List.of(donador)));
        when(donacionesClient.getTotalRecaudado())
                .thenReturn(Mono.just(BigDecimal.valueOf(45_000)));
        when(donacionesClient.getConteoDonaciones())
                .thenReturn(Mono.just(12L));

        // when & then
        webTestClient.get()
                .uri("/bff/portada")
                .exchange()
                .expectStatus().isOk()
                .expectBody(PortadaResponseDTO.class)
                .value(response -> {
                    assertThat(response.getCausasActivas()).hasSize(1);
                    assertThat(response.getCausasActivas().get(0).getTitulo())
                            .isEqualTo("Campaña Solidaria");
                    assertThat(response.getTopDonadores()).hasSize(1);
                    assertThat(response.getTopDonadores().get(0).getNombre())
                            .isEqualTo("María López");
                    assertThat(response.getMensajeError()).isNull();
                    assertThat(response.getResumen().getTotalRecaudado())
                            .isEqualByComparingTo(BigDecimal.valueOf(45_000));
                    assertThat(response.getResumen().getTotalDonaciones()).isEqualTo(12L);
                    assertThat(response.getResumen().getCausasActivas()).isEqualTo(1L);
                });
    }

    @Test
    @DisplayName("portadaFallbackCuandoServicioCaido: ms-donaciones caído → mensajeError no nulo y listas vacías")
    void portadaFallbackCuandoServicioCaido() {
        // given — Circuit Breaker disparó; el client ya ejecutó el fallbackMethod
        when(donacionesClient.getCausasActivas())
                .thenReturn(Mono.just(List.of()));
        when(donacionesClient.getTopDonadores(anyInt()))
                .thenReturn(Mono.just(List.of()));
        when(donacionesClient.getTotalRecaudado())
                .thenReturn(Mono.just(BigDecimal.ZERO));
        when(donacionesClient.getConteoDonaciones())
                .thenReturn(Mono.just(0L));

        // when & then
        webTestClient.get()
                .uri("/bff/portada")
                .exchange()
                .expectStatus().isOk()
                .expectBody(PortadaResponseDTO.class)
                .value(response -> {
                    assertThat(response.getCausasActivas()).isEmpty();
                    assertThat(response.getTopDonadores()).isEmpty();
                    assertThat(response.getMensajeError()).isNotNull();
                    assertThat(response.getMensajeError())
                            .containsIgnoringCase("no está disponible");
                });
    }
}
