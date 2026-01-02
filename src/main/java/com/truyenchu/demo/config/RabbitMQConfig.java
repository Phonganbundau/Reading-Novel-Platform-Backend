package com.truyenchu.demo.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Queue names
    public static final String NEW_CHAPTER_NOTIFICATION_QUEUE = "notification.new-chapter";
    public static final String NEW_CHAPTER_NOTIFICATION_DLQ = "notification.new-chapter.dlq";

    // Exchange names
    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        return factory;
    }

    // Exchange
    @Bean
    public TopicExchange notificationExchange() {
        return ExchangeBuilder
                .topicExchange(NOTIFICATION_EXCHANGE)
                .durable(true)
                .build();
    }

    // Queue for new chapter notifications
    @Bean
    public Queue newChapterNotificationQueue() {
        return QueueBuilder
                .durable(NEW_CHAPTER_NOTIFICATION_QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", NEW_CHAPTER_NOTIFICATION_DLQ)
                .build();
    }

    // Dead Letter Queue
    @Bean
    public Queue newChapterNotificationDLQ() {
        return QueueBuilder.durable(NEW_CHAPTER_NOTIFICATION_DLQ).build();
    }

    // Binding
    @Bean
    public Binding newChapterNotificationBinding() {
        return BindingBuilder
                .bind(newChapterNotificationQueue())
                .to(notificationExchange())
                .with("new-chapter");
    }
}

