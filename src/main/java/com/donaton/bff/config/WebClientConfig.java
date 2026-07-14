package com.donaton.bff.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfig {

    @Value("${MS_DONACIONES_URL:http://localhost:8084}")
    private String donacionesBaseUrl;

    @Value("${MS_AUTH_URL:http://localhost:8083}")
    private String authBaseUrl;

    @Bean("donacionesWebClient")
    public WebClient donacionesWebClient() {
        return WebClient.builder()
                .baseUrl(donacionesBaseUrl)
                .clientConnector(new ReactorClientHttpConnector(buildHttpClient()))
                .build();
    }

    @Bean("authWebClient")
    public WebClient authWebClient() {
        return WebClient.builder()
                .baseUrl(authBaseUrl)
                .clientConnector(new ReactorClientHttpConnector(buildHttpClient()))
                .build();
    }

    private HttpClient buildHttpClient() {
        return HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .responseTimeout(Duration.ofSeconds(3))
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(3, TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(3, TimeUnit.SECONDS)));
    }
}
