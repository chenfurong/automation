package com.ibm.automation.core.exception.handler;

import com.ibm.automation.core.exception.BaseException;

public class LoginTimeOutException extends BaseException {

	private static final long serialVersionUID = -5822026411566567622L;

	public LoginTimeOutException() {
		super();
	}

	public LoginTimeOutException(String message) {
		super(message);
	}

	public LoginTimeOutException(Throwable cause) {
		super(cause);
	}

	public LoginTimeOutException(String message, Throwable cause) {
		super(message, cause);
	}

}
