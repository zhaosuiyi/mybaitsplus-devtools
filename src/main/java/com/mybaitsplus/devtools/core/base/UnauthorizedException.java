package com.mybaitsplus.devtools.core.base;

import com.mybaitsplus.devtools.core.exception.BaseException;
import com.mybaitsplus.devtools.core.support.HttpCode;

@SuppressWarnings("serial")
public class UnauthorizedException extends BaseException {
	public UnauthorizedException() {
	}

	public UnauthorizedException(String message) {
		super(message);
	}

	public UnauthorizedException(String message, Exception e) {
		super(message, e);
	}

	@Override
	protected HttpCode getHttpCode() {
		return HttpCode.FORBIDDEN;
	}
}
