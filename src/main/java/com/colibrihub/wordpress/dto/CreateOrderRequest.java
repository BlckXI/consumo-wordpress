package com.colibrihub.wordpress.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Payload del endpoint POST /api/orders de Grupo A.
 */
@Data
public class CreateOrderRequest {

    // ── Datos del cliente ───────────────────────────────────────────────
    @NotBlank(message = "El nombre es obligatorio")
    private String firstName;

    @NotBlank(message = "El apellido es obligatorio")
    private String lastName;

    @Email(message = "Correo inválido")
    @NotBlank(message = "El correo es obligatorio")
    private String email;

    @NotBlank(message = "La dirección de entrega es obligatoria")
    private String deliveryAddress;

    // ── Datos del producto ──────────────────────────────────────────────
    @NotBlank(message = "El nombre del producto es obligatorio")
    private String productName;

    private String productDescription;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    private BigDecimal productPrice;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad mínima es 1")
    private Integer productQuantity;
}
