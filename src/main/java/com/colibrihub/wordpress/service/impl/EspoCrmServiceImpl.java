package com.colibrihub.wordpress.service.impl;

import com.colibrihub.wordpress.service.EspoCrmService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class EspoCrmServiceImpl implements EspoCrmService {
    private final WebClient webClient;

    public EspoCrmServiceImpl(@Qualifier("espoCrmWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Map<String, Object> createLead(String firstName, String lastName, String email, String description) {
        return webClient.post()
                .uri("/api/v1/Lead")
                .bodyValue(Map.of(
                        "firstName", firstName,
                        "lastName", lastName,
                        "status", "New",
                        "source", "Web Site",
                        "description", description,
                        "emailAddress", email
                ))
                .retrieve()
                .onStatus(s -> s.is4xxClientError(),
                        r -> r.bodyToMono(String.class)
                                .map(err -> new RuntimeException("Error EspoCRM: " +
                                        err)))
                .bodyToMono(Map.class)
                .block();

    }
}
