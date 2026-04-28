package com.colibrihub.wordpress.service;

import com.colibrihub.wordpress.dto.*;

import java.util.List;

public interface WordpressService {
    List<PostDto> getAllPosts();
    PostDto createPost(CreatePostRequest request);
    PagedResponse<PostDto> getPosts(int page, int size);
}
