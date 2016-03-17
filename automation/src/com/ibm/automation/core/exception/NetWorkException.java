package com.ibm.automation.core.exception;

public class NetWorkException extends BaseException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public NetWorkException(String message)
	{
		super(message);
	}
	public NetWorkException(Throwable cause)
	{
		super(cause);
	}
	public NetWorkException()
	{
		
	}
	public NetWorkException(String message, Throwable cause) {
		super(message, cause);
	}
}
