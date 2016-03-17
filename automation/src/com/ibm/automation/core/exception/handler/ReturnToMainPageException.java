package com.ibm.automation.core.exception.handler;

public class ReturnToMainPageException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5008092781427556056L;


	public ReturnToMainPageException() {
	}

	public ReturnToMainPageException(String arg0) {
		super(arg0);
	}

	public ReturnToMainPageException(Throwable arg0) {
		super(arg0);
	}

	
	public ReturnToMainPageException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
}