package com.colibrihub.wordpress.dto;

import lombok.Data;

@Data
public class CreatePostRequest {
    private String title;
    private String content;
    private String status;
}
