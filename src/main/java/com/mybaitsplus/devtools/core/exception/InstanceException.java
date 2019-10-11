package com.mybaitsplus.devtools.core.exception;

import com.mybaitsplus.devtools.core.support.HttpCode;

/**
 * 
 */
@SuppressWarnings("serial")
public class InstanceException extends BaseException {
    public InstanceException() {
        super();
    }

    public InstanceException(Throwable t) {
        super(t);
    }

    @Override
    protected HttpCode getHttpCode() {
        return HttpCode.INTERNAL_SERVER_ERROR;
    }
}
