package com.mybaitsplus.devtools.core.exception;


@SuppressWarnings("serial")
public class ExcelException extends  RuntimeException {

	public ExcelException() {
	}

	public ExcelException(String message) {
		super(message);
	}

	public ExcelException(String message, Throwable throwable) {
		super(message, throwable);
	}


}
