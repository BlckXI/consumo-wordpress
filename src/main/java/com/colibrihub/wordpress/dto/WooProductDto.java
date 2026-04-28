package com.colibrihub.wordpress.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WooProductDto {
    private Long id;
    private String name;
    private String status;
    private String permalink;
    @JsonProperty("regular_price")
    private String regularPrice;
    // getters y setters
}
