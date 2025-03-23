package com.hardware_today.custom.controller_exceptions;

public class NotFoundException extends RuntimeException {
	public NotFoundException(String msg) {
		super(msg);
	}
}