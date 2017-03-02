package com.zhy.smail.restful;

/**
 * Created by wenliz on 2017/1/22.
 */
public class RfResultEvent {
    public static final int OK = 0;
    public static final int FAIL = -1;

    private Object data;
    private Integer result;
    private String message;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
