package com.hardware_today.custom.controller_exceptions;

public class UnauthorizedException extends RuntimeException {
	public UnauthorizedException(String msg) {
		super(msg);
	}
}
