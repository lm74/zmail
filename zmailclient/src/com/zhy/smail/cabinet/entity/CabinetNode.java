package com.zhy.smail.cabinet.entity;

/**
 * Created by wenliz on 2017/3/2.
 */
public class CabinetNode {
    private Integer cabinetId;
    private Integer cabinetNo;

    public Integer getCabinetNo() {
        return cabinetNo;
    }

    public void setCabinetNo(Integer cabinetNo) {
        this.cabinetNo = cabinetNo;
    }

    private Integer count;

    public Integer getCabinetId() {
        return cabinetId;
    }

    public void setCabinetId(Integer cabinetId) {
        this.cabinetId = cabinetId;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
