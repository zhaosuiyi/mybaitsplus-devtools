package com.mybaitsplus.devtools.core.exception;


import com.mybaitsplus.devtools.core.support.HttpCode;

@SuppressWarnings("serial")
public class LoginException extends BaseException {
	public LoginException() {
	}

	public LoginException(String message) {
		super(message);
	}

	public LoginException(String message, Exception e) {
		super(message, e);
	}

	@Override
	protected HttpCode getHttpCode() {
		return HttpCode.LOGIN_FAIL;
	}
}
