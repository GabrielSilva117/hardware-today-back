package com.hardware_today.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQConfig {
    public static final String PAYMENT_EXCHANGE = "payment.exchange";
    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";

    public static final String PAYMENT_OUTCOME_QUEUE = "payment_outcome_dispatch";
    public static final String EMAIL_QUEUE = "email_dispatch";

    public static final String EMAIL_ROUTING_KEY = "email.create";

    @Bean
    public DirectExchange notificationExchange() { return new DirectExchange(NOTIFICATION_EXCHANGE); }

    @Bean
    public TopicExchange paymentExchange() { return new TopicExchange(PAYMENT_EXCHANGE); }

    @Bean
    public Queue emailQueue() { return new Queue(EMAIL_QUEUE, true); }

    /**
     * Deliberately not "payment_dispatch": that name is ht_payment_ms's own inbound dispatch
     * queue (same broker, same name = same physical queue), and this monolith has nothing that
     * should receive from it - declaring/binding it here previously caused ht_payment_ms's own
     * outbound card.created/card.failed events to loop back into its own inbound listener.
     * This queue gets those same outcome events instead, via its own name.
     */
    @Bean
    public Queue paymentOutcomeQueue() { return new Queue(PAYMENT_OUTCOME_QUEUE, true); }

    @Bean
    public Binding paymentOutcomeBinding() {
        return BindingBuilder.bind(paymentOutcomeQueue())
                .to(paymentExchange())
                .with("payment.card.*");
    }

    @Bean
    public Binding emailBinding(Queue emailQueue, DirectExchange notificationExchange) {
        return BindingBuilder
                .bind(emailQueue())
                .to(notificationExchange())
                .with(EMAIL_ROUTING_KEY);
    }
}
