package com.jih10157.omsdatabase.api;

public class OmsFailException extends RuntimeException {

    public final int code;
    public final String reason;

    public OmsFailException(int code, String reason) {
        this.code = code;
        this.reason = reason;
    }
}
