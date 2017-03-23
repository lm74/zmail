package com.zy.zmail.server.north;

/**
 * Created by wenliz on 2017/3/8.
 */
public class DoorResult {

    public static final int BUILDING_WRONG = 0x02;
    public static final int DATA_WRONG = 0x03;
    public static final int PACKET_PART = 0x04;
    public static final int REPEAT_LOGIN = 0x05;

    public static final int SUCCESS = 1;
    public static final int FAIL = 0;


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
    public int getCommandNo() {
        return commandNo;
    }

    public void setCommandNo(int commandNo) {
        this.commandNo = commandNo;
    }

    public int getBuildingNo() {
        return buildingNo;
    }

    public void setBuildingNo(int buildingNo) {
        this.buildingNo = buildingNo;
    }

    public int getUnitNo() {
        return unitNo;
    }

    public void setUnitNo(int unitNo) {
        this.unitNo = unitNo;
    }

    public int getFloorNo() {
        return floorNo;
    }

    public void setFloorNo(int floorNo) {
        this.floorNo = floorNo;
    }

    public int getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(int roomNo) {
        this.roomNo = roomNo;
    }

    private Object[] data;
    private int errorNo;

    public int getSectionNo() {
        return sectionNo;
    }

    public void setSectionNo(int sectionNo) {
        this.sectionNo = sectionNo;
    }

    private int errorMessage;
    private int commandNo;
    private int buildingNo;
    private int unitNo;
    private int floorNo;
    private int roomNo;
    private int sectionNo;
}