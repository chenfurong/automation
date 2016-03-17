package com.ibm.automation.core.exception;

/**
 * openstack用户认证异常。
 * @author xucd
 *
 */
@SuppressWarnings("serial")
public class AuthorizedException extends RuntimeException{

	public AuthorizedException() {
	}

	public AuthorizedException(String arg0) {
		super(arg0);
	}

	public AuthorizedException(Throwable arg0) {
		super(arg0);
	}

	
	public AuthorizedException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
}
