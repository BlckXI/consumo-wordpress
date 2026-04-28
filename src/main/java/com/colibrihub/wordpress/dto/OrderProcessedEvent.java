package com.colibrihub.wordpress.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Respuesta que Grupo B envía de vuelta a Grupo A.
 * Debe coincidir exactamente con RespuestaPedidoEvent de Grupo B.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderProcessedEvent {
    /** ID del pedido en la BD de Grupo A (pedidoId en Grupo B) */
    private Long pedidoId;

    /** ID de la orden creada en WooCommerce */
    private String wooOrderId;

    /** ID del contacto creado en EspoCRM */
    private String espoCrmContactId;

    /** "COMPLETADO" o "ERROR" */
    private String estado;

    /** Mensaje descriptivo del resultado */
    private String mensaje;
}
