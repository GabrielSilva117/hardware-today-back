package com.hardware_today.publishers;

import com.hardware_today.config.MQConfig;
import com.hardware_today.dto.PaymentDTO;
import com.hardware_today.dto.PaymentResponse;
import com.hardware_today.enums.PaymentStatus;
import com.hardware_today.enums.PaymentType;
import com.hardware_today.feign.PaymentFeign;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentPublisher {
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

    public PaymentResponse publishPayment(PaymentDTO paymentDTO) {
        try {
            PaymentResponse res = paymentFeign.payCart(paymentDTO);
            if (res == null) {
                // Guarantee a non-null websocket payload for frontend listeners.
                res = new PaymentResponse(PaymentStatus.FAILURE);
            }
            messagingTemplate.convertAndSend(
                    "/topic/payment/" + paymentDTO.getCartId(),
                    res
            );
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            PaymentResponse failure = new PaymentResponse(PaymentStatus.FAILURE);
            messagingTemplate.convertAndSend(
                    "/topic/payment/" + paymentDTO.getCartId(),
                    failure
            );
            return failure;
        }
    }
}
