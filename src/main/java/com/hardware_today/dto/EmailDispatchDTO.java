package com.hardware_today.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmailDispatchDTO {
    private String subject;
    private String body;
    private String to;
}
