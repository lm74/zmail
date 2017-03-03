package com.zhy.smail.lcp;

/**
 * Created by wenliz on 2017/1/20.
 */
public class BoxEntry {
    public BoxEntry(int boxNo, int status){
        this.boxNo = boxNo;
        this.status = status;
    }

    public int getBoxNo() {
        return boxNo;
    }

    public void setBoxNo(int boxNo) {
        this.boxNo = boxNo;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    private int boxNo;
    private int status;
    private int sequence;
    private int boxId;

    public int getBoxId() {
        return boxId;
    }

    public void setBoxId(int boxId) {
        this.boxId = boxId;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }
}
