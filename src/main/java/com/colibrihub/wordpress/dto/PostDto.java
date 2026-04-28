package com.colibrihub.wordpress.dto;

public record PostDto(
        Long id,
        String slug,
        String status,
        String date,
        String type,
        String link,
        String author,
        String comment_status,
        String ping_status,
        Guid guid){
}
