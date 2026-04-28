package com.colibrihub.wordpress.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApiResponse<T> {

    private Boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    public static <T> ApiResponse<T> success(String message, T data){
        var response = new ApiResponse<T>();

        response.success = true;
        response.data = data;
        response.message = message;
        response.timestamp = LocalDateTime.now();

        return response;
    }


    public static<T> ApiResponse<T> error(String message){
        var response = new ApiResponse<T>();

        response.success = false;
        response.data = null;
        response.message = message;
        response.timestamp = LocalDateTime.now();

        return response;
    }
}
