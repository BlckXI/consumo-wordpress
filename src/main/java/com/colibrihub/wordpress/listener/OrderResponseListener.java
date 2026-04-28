package com.colibrihub.wordpress.listener;

import com.colibrihub.wordpress.config.RabbitConfig;
import com.colibrihub.wordpress.dto.OrderProcessedEvent;
import com.colibrihub.wordpress.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Escucha la cola de respuesta que Grupo B publica
 * y actualiza el registro local en MySQL.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderResponseListener {

    private final OrderService orderService;

    @RabbitListener(queues = RabbitConfig.QUEUE_ORDERS_REPLY)
    public void handleOrderProcessed(OrderProcessedEvent event) {
        log.info("Respuesta recibida de Grupo B → pedido #{}: estado={}",
                event.getPedidoId(), event.getEstado());

        orderService.updateOrderStatus(
                event.getPedidoId(),
                event.getEstado(),
                event.getWooOrderId(),
                event.getEspoCrmContactId(),
                event.getMensaje()
        );
    }
}
