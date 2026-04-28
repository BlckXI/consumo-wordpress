package com.colibrihub.wordpress.controller;

import com.colibrihub.wordpress.dto.*;
import com.colibrihub.wordpress.service.PostService;
import com.colibrihub.wordpress.service.WordpressService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
public class WordpressController {

    private final WordpressService wordpressService;

    public WordpressController(WordpressService wordpressService){
        this.wordpressService = wordpressService;
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<PagedResponse<PostDto>>> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        int safeSize = Math.min(Math.max(size, 1), 100); //Entre 1 y 100
        int safePage = Math.min(page, 0); // No negativo

        PagedResponse<PostDto> result =
                wordpressService.getPosts(safePage, safeSize);

        return ResponseEntity.ok(ApiResponse.success("Posts Obtenidos!", result));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PostDto>> createPost(@RequestBody CreatePostRequest request){
        PostDto created = wordpressService.createPost(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Post creado en Wordpress", created));
    }


}
