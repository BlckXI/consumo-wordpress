package com.colibrihub.wordpress.controller;

import com.colibrihub.wordpress.dto.PostDto;
import com.colibrihub.wordpress.service.PostService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService){
        this.postService = postService;
    }

    @GetMapping
    public ResponseEntity<List<PostDto>> getAllPosts(){
        List<PostDto> posts = postService.getAllPosts();

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", String.valueOf(posts.size()));

        return new ResponseEntity<>(posts, headers, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<PostDto> getPostById(@PathVariable Long id){
        PostDto post = postService.getPostById(id);
        return new ResponseEntity<>(post, HttpStatus.OK);
    }
}
