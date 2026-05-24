package com.hardware_today.service;

import com.hardware_today.dto.CardPayment;
import com.hardware_today.dto.PaymentResponse;
import com.hardware_today.enums.PaymentStatus;
import com.hardware_today.enums.PaymentType;
import com.hardware_today.publishers.PaymentPublisher;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentPublisher paymentPublisher;
    private final CartService cartService;
    private final PurchaseOrderService purchaseOrderService;

    public void publishPayment(PaymentType paymentType, UUID activeCart, CardPayment cardPayment) {
        // new CardPayment(new BigDecimal("233.44"), "USD", PaymentType.CARD_CREDIT, activeCart, "1000")
        cardPayment.setCartId(activeCart);
        this.paymentPublisher.publishPayment(paymentType, cardPayment);
    }

    public void publishPayment(UUID activeCart, CardPayment cardPayment, HttpServletResponse response) {
        cardPayment.setCartId(activeCart);
        PaymentResponse paymentResponse = this.paymentPublisher.callPaymentMicroservice(cardPayment);
        if (paymentResponse != null && PaymentStatus.SUCCESS.equals(paymentResponse.getStatus())) {
            var order = this.purchaseOrderService.createFromCart(activeCart);
            paymentResponse.setPurchaseOrderId(order.getId());
            this.cartService.deleteCart(activeCart, true, response);
        }
        this.paymentPublisher.broadcastPaymentResult(activeCart, paymentResponse);
    }
}
