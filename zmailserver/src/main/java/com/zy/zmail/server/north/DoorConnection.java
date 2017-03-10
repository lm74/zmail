package com.zy.zmail.server.north;

import org.springframework.stereotype.Component;

/**
 * Created by wenliz on 2017/3/8.
 */
@Component
public class DoorConnection {
    private Integer protocolType;
    private String serverIp;
    private int buildingNo;
    private int unitNo;

    public DoorConnection(){
        protocolType = 0;
        buildingNo = 0;
        unitNo = 0;
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

    public Integer getProtocolType() {
        return protocolType;
    }

    public void setProtocolType(Integer protocolType) {
        this.protocolType = protocolType;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public Integer getServerPort() {
        return serverPort;
    }

    public void setServerPort(Integer serverPort) {
        this.serverPort = serverPort;
    }

    private Integer serverPort;
}
