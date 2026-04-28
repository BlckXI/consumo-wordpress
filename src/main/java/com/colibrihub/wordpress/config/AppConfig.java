package com.colibrihub.wordpress.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder){
        return builder
                .connectTimeout(Duration.ofSeconds(5))
                .readTimeout(Duration.ofSeconds(10))
                .errorHandler(new DefaultResponseErrorHandler())
                .build();
    }

    // Bean especifico para Wordpress (con base URL y Auth)
    @Bean("wordpressWebClient")
    public WebClient wordpressWebClient(
            @Value("${woocommerce.base-url}") String baseUrl,
            @Value("${wordpress.username}") String username,
            @Value("${wordpress.app-password}") String password
    ){
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeaders( h -> h.setBasicAuth(username, password.replace(" ", "")))
                .codecs(c -> c.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
                .build();
    }

    @Bean("wooWebClient")
    public WebClient wooWebClient(WebClient.Builder builder,
                                @Value("${woocommerce.base-url}") String baseUrl,
                                @Value("${woocommerce.consumer-key}") String key,
                                @Value("${woocommerce.consumer-secret}") String
                                        secret) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeaders(h -> h.setBasicAuth(key, secret))
                .codecs(c -> c.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
                .build();
    }

    // En WebClientConfig.java
    @Bean("espoCrmWebClient")
    public WebClient espoCrmWebClient(
            @Value("${espocrm.base-url}") String baseUrl,
            @Value("${espocrm.api-key}") String apiKey) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("X-Api-Key", apiKey)
                .defaultHeader("Content-Type", "application/json")
                .codecs(c -> c.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
                .build();
    }
}
