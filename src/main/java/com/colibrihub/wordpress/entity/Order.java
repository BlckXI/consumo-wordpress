package com.colibrihub.wordpress.entity;

import com.colibrihub.wordpress.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ── Datos del cliente ───────────────────────────────────────────────
    private String firstName;
    private String lastName;
    private String email;
    private String deliveryAddress;

    // ── Datos del producto ──────────────────────────────────────────────
    private String productName;
    private String productDescription;
    private BigDecimal productPrice;
    private Integer productQuantity;

    // ── Estado y referencias externas ──────────────────────────────────
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    /** ID de la orden creada en WooCommerce (lo informa Grupo B) */
    private String wooOrderId;

    /** ID del contacto creado en EspoCRM (lo informa Grupo B) */
    private String espoCrmContactId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.status    = OrderStatus.PENDING;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
