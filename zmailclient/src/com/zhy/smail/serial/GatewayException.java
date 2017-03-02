package com.zhy.smail.serial;

/**
 * Created by wenliz on 2017/1/20.
 */
public class GatewayException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = 7701132014354346907L;

    public GatewayException(String errorMessage) {
        super(errorMessage);
    }
}
