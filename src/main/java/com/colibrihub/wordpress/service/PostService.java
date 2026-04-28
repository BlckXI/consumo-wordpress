package com.colibrihub.wordpress.service;

import com.colibrihub.wordpress.dto.PostDto;

import java.util.List;

public interface PostService {
    List<PostDto> getAllPosts();
    PostDto getPostById(Long id);
    List<PostDto> getPost();
}
