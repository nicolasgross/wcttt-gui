package de.nicolasgross.wcttt.gui;

public class WctttGuiException extends Exception {

	public WctttGuiException() {
		super();
	}

	public WctttGuiException(String message) {
		super(message);
	}

	public WctttGuiException(String message, Throwable cause) {
		super(message, cause);
	}

	public WctttGuiException(Throwable cause) {
		super(cause);
	}

	protected WctttGuiException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
