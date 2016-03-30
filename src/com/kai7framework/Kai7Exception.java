package com.kai7framework;

public class Kai7Exception extends RuntimeException {

	public Kai7Exception() {
        super();
    }

    public Kai7Exception(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public Kai7Exception(String msg) {
        super(msg);
    }

    public Kai7Exception(Throwable arg0) {
        super(arg0);
    }

}
