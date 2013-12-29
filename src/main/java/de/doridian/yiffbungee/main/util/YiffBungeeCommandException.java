package de.doridian.yiffbungee.main.util;

public class YiffBungeeCommandException extends Exception {
	private static final long serialVersionUID = 1L;

	public YiffBungeeCommandException(String message, Throwable cause) {
		super(message, cause);
	}

	public YiffBungeeCommandException(String message) {
		super(message);
	}

	public YiffBungeeCommandException(Throwable cause) {
		super(cause);
	}
}
