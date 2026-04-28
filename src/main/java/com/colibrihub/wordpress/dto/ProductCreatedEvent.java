package com.colibrihub.wordpress.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
// NO necesita implements Serializable gracias a Jackson JSON
@Data
public class ProductCreatedEvent {
    private String productName;
    private BigDecimal price;
    private String description;
    private String contactEmail;
    private LocalDateTime timestamp;
    // Constructor vacío para Jackson
    public ProductCreatedEvent() {
        this.timestamp = LocalDateTime.now();
    }
    public ProductCreatedEvent(String productName, BigDecimal price,
                               String description, String contactEmail) {
        this.productName = productName;
        this.price = price;
        this.description = description;
        this.contactEmail = contactEmail;
        this.timestamp = LocalDateTime.now();
    }
// Getters y setters...
}