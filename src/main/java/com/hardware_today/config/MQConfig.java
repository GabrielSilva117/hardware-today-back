package com.hardware_today.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQConfig {
    public static String PAYMENT_EXCHANGE = "payment.exchange";
    public static String NOTIFICATION_EXCHANGE = "notification.exchange";

    public static String PAYMENT_QUEUE = "payment_dispatch";
    public static String EMAIL_QUEUE = "email_dispatch";

    public static String EMAIL_ROUTING_KEY = "email.create";

    @Bean
    public DirectExchange notificationExchange() { return new DirectExchange(NOTIFICATION_EXCHANGE); }

    @Bean
    public TopicExchange paymentExchange() { return new TopicExchange(PAYMENT_EXCHANGE); }

    @Bean
    public Queue emailQueue() { return new Queue(EMAIL_QUEUE, true); }

    @Bean
    public Queue paymentQueue() { return new Queue(PAYMENT_QUEUE, true); }

    @Bean
    public Binding paymentBinding() {
        return BindingBuilder.bind(paymentQueue())
                .to(paymentExchange())
                .with("payment.#");
    }

    @Bean
    public Binding emailBinding(Queue emailQueue, DirectExchange notificationExchange) {
        return BindingBuilder
                .bind(emailQueue())
                .to(notificationExchange())
                .with(EMAIL_ROUTING_KEY);
    }
}
