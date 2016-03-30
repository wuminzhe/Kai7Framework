package com.kai7framework.network;

import com.kai7framework.Kai7Exception;

public class HttpException extends Kai7Exception {
	public HttpException() {
        super();
    }

    public HttpException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public HttpException(String msg) {
        super(msg);
    }

    public HttpException(Throwable arg0) {
        super(arg0);
    }
}
