package com.hardware_today.listener;

import com.hardware_today.config.MQConfig;
import com.hardware_today.dto.PaymentDTO;
import com.hardware_today.dto.PaymentResponse;
import com.hardware_today.entity.PurchaseOrder;
import com.hardware_today.enums.PaymentStatus;
import com.hardware_today.publishers.PaymentPublisher;
import com.hardware_today.service.CartService;
import com.hardware_today.service.PurchaseOrderService;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * ht_payment_ms's CardProcessor always returns PROCESSING for CARD payments - the real
 * outcome only becomes known later, when Stripe's webhook resolves the PaymentIntent.
 * That resolution is published onto payment.exchange (routing keys payment.card.created /
 * payment.card.failed); this listener is what lets the browser ever learn the final result.
 */
@Component
@RequiredArgsConstructor
public class PaymentOutcomeListener {
    private static final Logger logger = LoggerFactory.getLogger(PaymentOutcomeListener.class);

    private final PurchaseOrderService purchaseOrderService;
    private final CartService cartService;
    private final PaymentPublisher paymentPublisher;

    @RabbitListener(queues = MQConfig.PAYMENT_OUTCOME_QUEUE, ackMode = "MANUAL")
    public void onPaymentOutcome(PaymentDTO dto, Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();

        try {
            PaymentResponse response = routingKey.endsWith(".created")
                    ? handleSucceeded(dto)
                    : handleFailed(dto);
            paymentPublisher.broadcastPaymentResult(dto.getCartId(), response);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            logger.error("Failed to handle payment outcome for cartId {}", dto.getCartId(), e);
            channel.basicNack(deliveryTag, false, false);
        }
    }

    private PaymentResponse handleSucceeded(PaymentDTO dto) throws Exception {
        PurchaseOrder order = purchaseOrderService.createFromCart(dto.getCartId());
        // Not the active-request cart: there is no HttpServletResponse to clear the
        // "active_cart" cookie against here, and nothing server-side reads that cookie anyway.
        cartService.deleteCart(dto.getCartId(), false, null);
        return new PaymentResponse(PaymentStatus.SUCCESS, order.getId());
    }

    private PaymentResponse handleFailed(PaymentDTO dto) {
        return new PaymentResponse(PaymentStatus.FAILURE, null);
    }
}
