package com.employee.onboarding.userAuthentication.exception;

public class RoleAlreadyExistsException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RoleAlreadyExistsException(String message)
	{
		super(message);
	}
}
