package com.zhy.smail.lcp;

/**
 * Created by wenliz on 2017/1/20.
 */
public class LcResult {
    public static final int SUCCESS = 0;
    public static final int FAIL = -1;

    private int boardNo;
    private String version;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getBoardNo() {
        return boardNo;
    }

    public void setBoardNo(int boardNo) {
        this.boardNo = boardNo;
    }

    public Object[] getData() {
        return data;
    }

    public void setData(Object[] data) {
        this.data = data;
    }

    public int getErrorNo() {
        return errorNo;
    }

    public void setErrorNo(int errorNo) {
        this.errorNo = errorNo;
    }

    public int getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(int errorMessage) {
        this.errorMessage = errorMessage;
    }

    private Object[] data;
    private int errorNo;
    private int errorMessage;
}
