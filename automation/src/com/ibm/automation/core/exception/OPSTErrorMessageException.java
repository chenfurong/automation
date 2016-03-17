package com.ibm.automation.core.exception;

public class OPSTErrorMessageException extends OPSTBaseException {

	private static final long serialVersionUID = 2702062870335681382L;

	public OPSTErrorMessageException() {
		super();
	}

	public OPSTErrorMessageException(String message) {
		super(message);
	}

	public OPSTErrorMessageException(Throwable cause) {
		super(cause);
	}

	public OPSTErrorMessageException(String message, Throwable cause) {
		super(message, cause);
	}
}