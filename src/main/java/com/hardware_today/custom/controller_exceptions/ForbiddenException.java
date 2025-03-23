package com.hardware_today.custom.controller_exceptions;

public class ForbiddenException extends RuntimeException {
	public ForbiddenException(String msg) {
		super(msg);
	}
}
