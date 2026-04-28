package com.colibrihub.wordpress.service.impl;

import com.colibrihub.wordpress.config.RabbitConfig;
import com.colibrihub.wordpress.dto.ProductCreatedEvent;
import com.colibrihub.wordpress.service.ProductEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProductEventPublisherImpl implements ProductEventPublisher {
    private final RabbitTemplate rabbitTemplate;
    public ProductEventPublisherImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publishProductCreated(ProductCreatedEvent event) {
        log.info("Publicando evento ProductCreated: {}",
                event.getProductName());
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_NAME,  // exchange de productos, no de pedidos
                RabbitConfig.ROUTING_KEY,
                event
        );
        log.info("Evento publicado exitosamente");
    }
}