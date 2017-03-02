package com.zhy.smail.common.json;

/**
 * Created by wenliz on 2017/1/22.
 */

public class JsonResult {
    public static Integer FAIL = -1;
    public static Integer OK = 0;

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

    public static JsonResult getInstance(Integer result, String message) {
        JsonResult json = new JsonResult();
        json.setResult(result);
        json.setMessage(message);
        json.setData(null);
        return json;
    }

    public static JsonResult getInstance(Integer result) {
        return getInstance(result, "");
    }

    public static JsonResult getInstance() {
        return getInstance(OK, "");
    }
}