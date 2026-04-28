package com.colibrihub.wordpress.service.impl;


import com.colibrihub.wordpress.dto.*;
import com.colibrihub.wordpress.service.WordpressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class WordpressServiceImpl implements WordpressService {

    private final WebClient webClient;

    public WordpressServiceImpl(@Qualifier("wordpressWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public List<PostDto> getAllPosts() {
        return webClient.get()
                .uri("/wp-json/wp/v2/posts")
                .retrieve()
                .bodyToFlux(PostDto.class)
                .collectList()
                .block();
    }

    //NUEVO: Crear un nuevo post en Wordpress
    @Override
    @CacheEvict(value = "wordpress-posts", allEntries = true)
    public PostDto createPost(CreatePostRequest request){
        return webClient.post()
                .uri("/wp-json/wp/v2/posts")
                .bodyValue(Map.of("title", request.getTitle(),
                        "content", request.getContent(),
                        "status", request.getStatus() != null ? request.getStatus() : "draft" ))
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response -> response.bodyToMono(String.class)
                        .map(body -> new RuntimeException("Wordpress error:" + body)))
                .bodyToMono(PostDto.class)
                .block();
    }

    @Override
    @Cacheable(value = "wordpress-posts", key = "#page + '-' + #size")
    public PagedResponse<PostDto> getPosts(int page, int size) {
        log.info("Getting posts for page {}", page);
        log.info("CACHE MISS: Llamando a WordPress para page={}, size={}", page, size);

        int wpPage = page + 1;

        ResponseEntity<List<PostDto>> response = webClient.get()
                .uri( uri -> uri
                        .path("/wp-json/wp/v2/posts")
                        .queryParam("per_page", size)
                        .queryParam("page", wpPage)
                        .build())
                .retrieve()
                .onStatus(s-> s.is4xxClientError(),
                        r -> r.bodyToMono(String.class).map(RuntimeException::new))
                .toEntityList(PostDto.class)
                .block();

        List<PostDto> posts = response.getBody();
        String totalHeader = response.getHeaders().getFirst("X-WP-Total");
        long total = totalHeader != null ? Long.parseLong(totalHeader) : 0L;

        return PagedResponse.of(posts, page, size, total);
    }


}
