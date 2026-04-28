package com.colibrihub.wordpress.entity;

import com.colibrihub.wordpress.enums.LogStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Entity // Marca esta clase como una tabla en la BD
@Table(name = "integration_logs") // Nombre de la tabla (opcional)
@Setter
@Getter
public class IntegrationLog {
    @Id // Clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment
    private Long id;

    @Column(nullable = false) // Columna NOT NULL
    private String targetSystem; // WooCommerce, EspoCRM, etc.
    @Column(nullable = false)
    private String operation; // CREATE_PRODUCT, CREATE_LEAD, etc.
    @Enumerated(EnumType.STRING) // Guarda el nombre del enum, no el ordinal
    @Column(nullable = false)
    private LogStatus status; // SUCCESS, FAILED, PARTIAL
    @Column(columnDefinition = "TEXT") // Para textos largos
    private String errorMessage;
    @Column(columnDefinition = "TEXT")
    private String requestData; // JSON del request
    @Column(nullable = false, updatable = false) // No se puede actualizar después
    private LocalDateTime timestamp;

    // Muchos logs pertenecen a un producto
    @ManyToOne
    @JoinColumn(name = "product_id") // Columna FK en integration_logs
    private Product product;

    @PrePersist // Se ejecuta antes de INSERT
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }

}
