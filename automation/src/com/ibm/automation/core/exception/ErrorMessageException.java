package com.ibm.automation.core.exception;

public class ErrorMessageException extends BaseException {

	private static final long serialVersionUID = 2702062870335681382L;

	public ErrorMessageException() {
		super();
	}

	public ErrorMessageException(String message) {
		super(message);
	}

	public ErrorMessageException(Throwable cause) {
		super(cause);
	}

	public ErrorMessageException(String message, Throwable cause) {
		super(message, cause);
	}

}
