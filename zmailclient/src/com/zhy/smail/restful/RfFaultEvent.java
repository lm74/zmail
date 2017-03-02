package com.zhy.smail.restful;

/**
 * Created by wenliz on 2017/1/22.
 */
public class RfFaultEvent {
    private int errorNo;
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getErrorNo() {
        return errorNo;
    }

    public void setErrorNo(int errorNo) {
        this.errorNo = errorNo;
    }
}
