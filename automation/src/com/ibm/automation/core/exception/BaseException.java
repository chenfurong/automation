package com.ibm.automation.core.exception;

/**
 * Title:BaseException<br>
 * Description:基础异常类<br>
 * Company: IBM<br>
 * Copyright @ 2012 .All rights reserved. <br>
 * @author Liuhl<br>
 * @version 2006-6-15 1.0
 */
public class BaseException extends RuntimeException {

	/**
	 * <code>serialVersionUID</code> 的注释
	 */
	private static final long serialVersionUID = -5822026411566567622L;

	public BaseException() {
		super();
	}

	public BaseException(String message) {
		super(message);
	}

	public BaseException(Throwable cause) {
		super(cause);
	}

	public BaseException(String message, Throwable cause) {
		super(message, cause);
	}

}
