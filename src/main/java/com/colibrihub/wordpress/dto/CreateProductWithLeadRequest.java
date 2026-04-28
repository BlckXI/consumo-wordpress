package com.colibrihub.wordpress.dto;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductWithLeadRequest {
    // Datos del producto
    @NotBlank(message = "El nombre del producto es obligatorio")
    private String productName;
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private BigDecimal price;
    private String productDescription;
    // Datos del lead
    @NotBlank(message = "El nombre del contacto es obligatorio")
    private String contactFirstName;
    @NotBlank(message = "El apellido del contacto es obligatorio")
    private String contactLastName;
    @Email(message = "Email inválido")
    @NotBlank(message = "El email es obligatorio")
    private String contactEmail;

}