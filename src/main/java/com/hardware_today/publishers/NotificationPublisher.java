package com.hardware_today.publishers;

import com.hardware_today.config.MQConfig;
import com.hardware_today.dto.EmailDispatchDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationPublisher {
    private static final Logger log = LoggerFactory.getLogger(NotificationPublisher.class);

    public final RabbitTemplate rabbitTemplate;

    public void publishNotification (EmailDispatchDTO event) {
        try {
            rabbitTemplate.convertAndSend(
                    MQConfig.NOTIFICATION_EXCHANGE,
                    MQConfig.EMAIL_ROUTING_KEY,
                    event
            );
        } catch (Exception e) {
            log.error("Failed to publish notification event", e);
        }
    }
}
