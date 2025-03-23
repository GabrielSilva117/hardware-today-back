package com.hardware_today.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String errorMsg;
    
    public AuthResponse(String errorMsg) {
    	this.errorMsg = errorMsg;
    }
}