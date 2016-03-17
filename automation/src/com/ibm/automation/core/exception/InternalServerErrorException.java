package com.ibm.automation.core.exception;

public class InternalServerErrorException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2204311101782266905L;


	public InternalServerErrorException() {
	}

	public InternalServerErrorException(String arg0) {
		super(arg0);
	}

	public InternalServerErrorException(Throwable arg0) {
		super(arg0);
	}

	
	public InternalServerErrorException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
}