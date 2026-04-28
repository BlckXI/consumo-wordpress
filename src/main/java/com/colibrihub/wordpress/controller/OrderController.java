package com.colibrihub.wordpress.controller;

import com.colibrihub.wordpress.dto.ApiResponse;
import com.colibrihub.wordpress.dto.CreateOrderRequest;
import com.colibrihub.wordpress.dto.OrderResponseDto;
import com.colibrihub.wordpress.entity.Order;
import com.colibrihub.wordpress.repository.OrderRepository;
import com.colibrihub.wordpress.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponseDto>> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {

        Order order = orderService.createOrder(request);

        OrderResponseDto dto = new OrderResponseDto(
                order.getId(),
                order.getStatus().name(),
                order.getCreatedAt()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Pedido creado exitosamente", dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Order>> getOrder(@PathVariable Long id) {
        return orderRepository.findById(id)
                .map(o -> ResponseEntity.ok(ApiResponse.success("Pedido encontrado", o)))
                .orElse(ResponseEntity.notFound().build());
    }
}