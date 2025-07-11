package com.hardware_today.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class UserDTO {
	private UUID id;
	private String firstName;
    private String lastName;
}
