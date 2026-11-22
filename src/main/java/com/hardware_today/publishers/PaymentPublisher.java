package com.hardware_today.publishers;

import com.hardware_today.config.MQConfig;
import com.hardware_today.dto.PaymentDTO;
import com.hardware_today.dto.PaymentResponse;
import com.hardware_today.enums.PaymentStatus;
import com.hardware_today.enums.PaymentType;
import com.hardware_today.feign.PaymentFeign;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentPublisher {
    private static final Logger log = LoggerFactory.getLogger(PaymentPublisher.class);

    private final RabbitTemplate rabbitTemplate;
    private final SimpMessagingTemplate messagingTemplate;
    private final PaymentFeign paymentFeign;

    public void publishPayment(PaymentType type, PaymentDTO paymentDTO) {
        String routingKey = String.format("payment.%s", type.getPath());

        rabbitTemplate.convertAndSend(
                MQConfig.PAYMENT_EXCHANGE,
                routingKey,
                paymentDTO
        );
    }

    /**
     * Calls the payment microservice only (no WebSocket). Caller broadcasts after PO / cart side-effects.
     */
    public PaymentResponse callPaymentMicroservice(PaymentDTO paymentDTO) {
        try {
            PaymentResponse res = paymentFeign.payCart(paymentDTO);
            if (res == null) {
                return new PaymentResponse(PaymentStatus.FAILURE, null);
            }
            return res;
        } catch (Exception e) {
            log.error("Failed to call payment microservice for cart {}", paymentDTO.getCartId(), e);
            return new PaymentResponse(PaymentStatus.FAILURE, null);
        }
    }

    public void broadcastPaymentResult(UUID cartId, PaymentResponse response) {
        messagingTemplate.convertAndSend(
                "/topic/payment/" + cartId,
                response
        );
    }
}
