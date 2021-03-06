package com.github.gpluscb.ggjava.api.exception;

/**
 * Represents a deserialization failure. This should not occur with api responses.
 */
public class DeserializationException extends Exception {
	public DeserializationException() {
	}

	public DeserializationException(String message) {
		super(message);
	}

	public DeserializationException(String message, Throwable cause) {
		super(message, cause);
	}

	public DeserializationException(Throwable cause) {
		super(cause);
	}

	public DeserializationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
