package com.chensoul.sharedlib.security.util;

public record ErrorMessage(String message) {

	public static ErrorMessage from(final String message) {
		return new ErrorMessage(message);
	}
}
