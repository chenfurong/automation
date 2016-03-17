package com.ibm.automation.core.exception;

public class ParamErrorException extends Exception {

	/**
	 * @author warren
	 * @
	 */
	private static final long serialVersionUID = -5680767870552397351L;
	public ParamErrorException()
	{
		
	}
	public ParamErrorException(String ex)
	{
		super(ex);
		//System.out.println("error:"+ex);
	}
	public ParamErrorException(Throwable arg0) {
		super(arg0);
	}
	public ParamErrorException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
