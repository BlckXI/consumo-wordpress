package com.colibrihub.wordpress.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Evento que Grupo A publica en RabbitMQ.
 * Los nombres de campo coinciden con PedidoEvent de Grupo B.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreatedEvent {

    // ── Datos del pedido ────────────────────────────────────────────────
    /** ID local del pedido en la BD de Grupo A */
    private Long pedidoId;

    // ── Datos del cliente ───────────────────────────────────────────────
    private String nombre;
    private String apellidos;
    private String direccionEntrega;
    private String correoElectronico;

    // ── Datos del producto ──────────────────────────────────────────────
    private String nombreProducto;
    private String descripcionProducto;
    private BigDecimal precioProducto;
    private Integer cantidadProducto;

    private LocalDateTime timestamp;
}
