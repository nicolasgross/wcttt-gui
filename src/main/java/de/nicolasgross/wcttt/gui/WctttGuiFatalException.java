package de.nicolasgross.wcttt.gui;

public class WctttGuiFatalException extends RuntimeException {

	public WctttGuiFatalException() {
		super();
	}

	public WctttGuiFatalException(String message) {
		super(message);
	}

	public WctttGuiFatalException(String message, Throwable cause) {
		super(message, cause);
	}

	public WctttGuiFatalException(Throwable cause) {
		super(cause);
	}

	protected WctttGuiFatalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
