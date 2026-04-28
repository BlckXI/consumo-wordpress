package com.colibrihub.wordpress.service;

import com.colibrihub.wordpress.config.RabbitConfig;
import com.colibrihub.wordpress.dto.CreateOrderRequest;
import com.colibrihub.wordpress.dto.OrderCreatedEvent;
import com.colibrihub.wordpress.entity.Order;
import com.colibrihub.wordpress.enums.OrderStatus;
import com.colibrihub.wordpress.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final RabbitTemplate  rabbitTemplate;

    // Inyectamos el servicio de correo aquí arriba con las demás dependencias
    private final EmailService emailService;

    /**
     * 1. Guarda el pedido en MySQL (estado PENDING).
     * 2. Publica el evento en RabbitMQ para que Grupo B lo procese.
     * 3. Envía el correo de confirmación al cliente.
     */
    @Transactional
    public Order createOrder(CreateOrderRequest request) {

        // ── PASO 1: persistir localmente ─────────────────────────────────
        Order order = new Order();
        order.setFirstName(request.getFirstName());
        order.setLastName(request.getLastName());
        order.setEmail(request.getEmail());
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setProductName(request.getProductName());
        order.setProductDescription(request.getProductDescription());
        order.setProductPrice(request.getProductPrice());
        order.setProductQuantity(request.getProductQuantity());
        Order saved = orderRepository.save(order);

        log.info("Pedido #{} guardado en MySQL con estado PENDING", saved.getId());

        // ── PASO 2: publicar evento ──────────────────────────────────────
        OrderCreatedEvent event = new OrderCreatedEvent(
                saved.getId(),
                saved.getFirstName(),
                saved.getLastName(),
                saved.getDeliveryAddress(),
                saved.getEmail(),
                saved.getProductName(),
                saved.getProductDescription(),
                saved.getProductPrice(),
                saved.getProductQuantity(),
                LocalDateTime.now()
        );

        rabbitTemplate.convertAndSend(
                RabbitConfig.SHARED_EXCHANGE,
                RabbitConfig.RK_ORDERS_OUT,
                event
        );

        log.info("Evento publicado en RabbitMQ → exchange='{}' routingKey='{}'",
                RabbitConfig.SHARED_EXCHANGE, RabbitConfig.RK_ORDERS_OUT);

        // ── PASO 3: Acción de envío de correo ────────────────────────────
        try {
            String asunto = "Confirmación de Pedido #" + saved.getId();
            String mensaje = "Hola " + request.getFirstName() + ",\n\nHemos recibido tu pedido de: "
                    + request.getProductName() + ".\nPronto te notificaremos sobre el estado de la entrega en: "
                    + request.getDeliveryAddress() + ".\n\nGracias por tu compra.";

            emailService.sendEmail(request.getEmail(), asunto, mensaje);
            log.info("Correo de confirmación enviado exitosamente a: {}", request.getEmail());
        } catch (Exception e) {
            log.error("El pedido se creó en BD y RabbitMQ, pero el correo falló: {}", e.getMessage());
        }

        return saved;
    }

    /**
     * Actualiza el pedido cuando Grupo B responde con la confirmación.
     */
    @Transactional
    public void updateOrderStatus(Long orderId, String status,
                                  String wooOrderId, String espoCrmContactId,
                                  String message) {
        orderRepository.findById(orderId).ifPresentOrElse(order -> {
            if ("COMPLETADO".equalsIgnoreCase(status)) {
                order.setStatus(OrderStatus.COMPLETED);
            } else {
                order.setStatus(OrderStatus.FAILED);
            }
            order.setWooOrderId(wooOrderId);
            order.setEspoCrmContactId(espoCrmContactId);
            orderRepository.save(order);
            log.info("Pedido #{} actualizado a {} (woo={}, crm={})",
                    orderId, order.getStatus(), wooOrderId, espoCrmContactId);
        }, () -> log.warn("Pedido #{} no encontrado al intentar actualizar estado", orderId));
    }
}