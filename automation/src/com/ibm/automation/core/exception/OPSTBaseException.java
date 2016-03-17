package com.ibm.automation.core.exception;



/**
 * Title:OPSTBaseException<br>
 * Description:OpenStack Base Exception<br>
 * 
 * Company: IBM<br>
 * Copyright @ 2012 .All rights reserved. <br>
 * 
 * @author YuanHui<br>
 * @version 2014/04/24 1.0
 */
public class OPSTBaseException extends Exception {

	/**
	 * <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -5822026411566567622L;

	public OPSTBaseException() {
		super();
	}

	public OPSTBaseException(String message) {
		super(message);
	}

	public OPSTBaseException(Throwable cause) {
		super(cause);
	}

	public OPSTBaseException(String message, Throwable cause) {
		super(message, cause);
	}

}
