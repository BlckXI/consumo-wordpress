package com.colibrihub.wordpress.service;

import com.colibrihub.wordpress.dto.ProductCreatedEvent;

public interface ProductEventPublisher {
    void publishProductCreated(ProductCreatedEvent event);

}
