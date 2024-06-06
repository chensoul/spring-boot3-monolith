package com.chensoul.monolith.infrastructure.validation;

/**
 * DataValidation Exception
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since 0.0.1
 */
public class DataValidationException extends RuntimeException {

	private static final long serialVersionUID = 7659985660312721830L;

	public DataValidationException(String message) {
		super(message);
	}

	public DataValidationException(String message, Throwable cause) {
		super(message, cause);
	}
}
