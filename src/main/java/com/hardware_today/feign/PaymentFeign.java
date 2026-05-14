package com.hardware_today.feign;

import com.hardware_today.dto.PaymentDTO;
import com.hardware_today.dto.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ht-payment-ms", url= "${services.payment.url}")
public interface PaymentFeign {
    @PostMapping("/ht-payment-ms/payment")
    PaymentResponse payCart(@RequestBody PaymentDTO body);
}
