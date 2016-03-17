package com.ibm.automation.core.exception;

/**
 * Access OpenStack Authorize Excetion
 * 
 * @author YuanHui
 *
 */
@SuppressWarnings("serial")
public class OPSTAuthorizedException extends OPSTBaseException{

	public OPSTAuthorizedException() {
	}

	public OPSTAuthorizedException(String arg0) {
		super(arg0);
	}

	public OPSTAuthorizedException(Throwable arg0) {
		super(arg0);
	}

	
	public OPSTAuthorizedException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
}
