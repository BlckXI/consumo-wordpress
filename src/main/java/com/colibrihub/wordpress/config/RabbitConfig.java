package com.colibrihub.wordpress.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // ── Pedidos (coordinación Grupo A ↔ Grupo B) ──────────────────────────
    public static final String SHARED_EXCHANGE    = "elearning.grupoSteven.pedido.events";
    public static final String QUEUE_ORDERS_OUT   = "queue.elearning.grupoSteven.pedido.nuevo";
    public static final String RK_ORDERS_OUT      = "elearning.grupoSteven.pedido.nuevo";
    public static final String QUEUE_ORDERS_REPLY = "queue.elearning.grupoSteven.pedido.respuesta";
    public static final String RK_ORDERS_REPLY    = "elearning.grupoSteven.pedido.respuesta";

    // ── Productos (ya existía) ─────────────────────────────────────────────
    public static final String EXCHANGE_NAME = "product.events";
    public static final String QUEUE_NAME    = "product.created.queue";
    public static final String ROUTING_KEY   = "product.created";
    public static final String DLQ_NAME      = "product.created.dlq";
    public static final String DLQ_EXCHANGE  = "product.dlx";

    // ── Beans pedidos ──────────────────────────────────────────────────────
    @Bean
    public TopicExchange sharedExchange() {
        return new TopicExchange(SHARED_EXCHANGE, true, false);
    }

    @Bean
    public Queue ordersOutQueue() {
        return QueueBuilder.durable(QUEUE_ORDERS_OUT).build();
    }

    @Bean
    public Binding ordersOutBinding(Queue ordersOutQueue, TopicExchange sharedExchange) {
        return BindingBuilder.bind(ordersOutQueue).to(sharedExchange).with(RK_ORDERS_OUT);
    }

    @Bean
    public Queue ordersReplyQueue() {
        return QueueBuilder.durable(QUEUE_ORDERS_REPLY).build();
    }

    @Bean
    public Binding ordersReplyBinding(Queue ordersReplyQueue, TopicExchange sharedExchange) {
        return BindingBuilder.bind(ordersReplyQueue).to(sharedExchange).with(RK_ORDERS_REPLY);
    }

    // ── Beans productos ────────────────────────────────────────────────────
    @Bean
    public TopicExchange productExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue productCreatedQueue() {
        return QueueBuilder.durable(QUEUE_NAME)
                .withArgument("x-dead-letter-exchange", DLQ_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", "product.created.failed")
                .build();
    }

    @Bean
    public Queue deadLetterQueue() {
        return new Queue(DLQ_NAME, true);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DLQ_EXCHANGE);
    }

    @Bean
    public Binding dlqBinding(@Qualifier("deadLetterQueue") Queue deadLetterQueue,
                              @Qualifier("deadLetterExchange") DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange)
                .with("product.created.failed");
    }

    @Bean
    public Binding productBinding(@Qualifier("productCreatedQueue") Queue productCreatedQueue,
                                  @Qualifier("productExchange") TopicExchange productExchange) {
        return BindingBuilder.bind(productCreatedQueue).to(productExchange).with(ROUTING_KEY);
    }

    // ── Serialización ──────────────────────────────────────────────────────
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}