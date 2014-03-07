package com.ada.sqlbuilder.builder;

public class Validate {
	private static final String DEFAULT_IS_NULL_EX_MESSAGE = "The validated object is null";

	public static <T> T notNull(T object) {
		return notNull(object, DEFAULT_IS_NULL_EX_MESSAGE);
	}

	public static <T> T notNull(T object, String message, Object... values) {
		if (object == null) {
			throw new NullPointerException(String.format(message, values));
		}
		return object;
	}
}
