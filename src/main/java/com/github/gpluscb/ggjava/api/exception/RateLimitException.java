package com.github.gpluscb.ggjava.api.exception;

import com.github.gpluscb.ggjava.internal.utils.Checks;
import com.google.gson.JsonObject;

import javax.annotation.Nonnull;

public class RateLimitException extends RequestFailureException {
	/**
	 * @throws if jsonResponse is null
	 */
	public RateLimitException(@Nonnull JsonObject jsonResponse) {
		super(jsonResponse, jsonResponse.toString());

		Checks.nonNull(jsonResponse, "jsonResponse");
	}
}
