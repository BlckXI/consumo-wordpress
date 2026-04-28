package com.colibrihub.wordpress.service.impl;

import com.colibrihub.wordpress.dto.CreateProductRequest;
import com.colibrihub.wordpress.dto.WooProductDto;
import com.colibrihub.wordpress.service.WooCommerceService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class WooCommerceServiceImpl implements WooCommerceService {

    private final WebClient webClient;

    public WooCommerceServiceImpl(@Qualifier("wooWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    // Crear un Producto en WooCommerce:
    @Override
    public WooProductDto createProduct(CreateProductRequest request) {
        return webClient.post()
                .uri("/wp-json/wc/v3/products")
                .bodyValue(Map.of(
                        "name", request.getName(),
                        "type", "simple",
                        "regular_price", request.getPrice().toString(),
                        "description", request.getDescription(),
                        "status", "publish"
                ))
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response -> response.bodyToMono(String.class)
                        .map(body -> new RuntimeException("Wordpress error:" + body)))
                .bodyToMono(WooProductDto.class)
                .block();
    }

}
