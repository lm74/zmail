package com.zy.zmail.server.north;

import java.util.Date;

/**
 * Created by wenliz on 2017/3/8.
 */
public class DoorMessage {
    private Integer sectionNo;
    private Integer buildingNo;
    private Integer unitNo;
    private Integer floorNo;
    private Integer roomNo;
    private Integer cabinetNo;
    private Integer boxNo;
    private Integer commandNo;
    private Object data;
    private Date deliveryTime;
    private byte operateType;

    public byte getOperateType() {
        return operateType;
    }

    public void setOperateType(byte operateType) {
        this.operateType = operateType;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public DoorMessage(){
        sectionNo = 1;
        buildingNo = 0;
        unitNo = 0;
        floorNo = 0;
        roomNo = 0;
        cabinetNo = 0;
        boxNo = 0;
    }

    public Integer getCommandNo() {
        return commandNo;
    }

    public void setCommandNo(Integer commandNo) {
        this.commandNo = commandNo;
    }

    public Integer getSectionNo() {
        return sectionNo;
    }

    public void setSectionNo(Integer sectionNo) {
        this.sectionNo = sectionNo;
    }

    public Integer getBuildingNo() {
        return buildingNo;
    }

    public void setBuildingNo(Integer buildingNo) {
        if(buildingNo ==null){
            buildingNo = 0;
        }
        this.buildingNo = buildingNo;
    }

    public Integer getUnitNo() {
        return unitNo;
    }

    public void setUnitNo(Integer unitNo) {
        if(unitNo ==null){
            unitNo = 0;
        }
        this.unitNo = unitNo;
    }

    public Integer getFloorNo() {
        return floorNo;
    }

    public void setFloorNo(Integer floorNo) {

        this.floorNo = floorNo;
    }

    public Integer getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(Integer roomNo) {
        this.roomNo = roomNo;
    }

    public Date getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Date deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public Integer getCabinetNo() {
        return cabinetNo;
    }

    public void setCabinetNo(Integer cabinetNo) {
        if(cabinetNo == null){
            cabinetNo = 0;
        }
        this.cabinetNo = cabinetNo;
    }

    public Integer getBoxNo() {
        return boxNo;
    }

    public void setBoxNo(Integer boxNo) {
        if(boxNo == null){
            boxNo = 0;
        }
        this.boxNo = boxNo;
    }
}
