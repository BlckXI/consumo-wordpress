package com.colibrihub.wordpress.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDto {
    private Long localId;
    private String status;
    private LocalDateTime createdAt;
}
