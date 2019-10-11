package com.mybaitsplus.devtools.core.exception;

/**
 * FTP异常
 * 
 *
 * @author ZHAO YI
 * @version 2017年5月12日 
 */
@SuppressWarnings("serial")
public class FtpException extends RuntimeException {
	public FtpException() {
	}

	public FtpException(String message) {
		super(message);
	}

	public FtpException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
