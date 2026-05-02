package com.hardware_today.dto;

import com.hardware_today.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    PaymentStatus status;
}
