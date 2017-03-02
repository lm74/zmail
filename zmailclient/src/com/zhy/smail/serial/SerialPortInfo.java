package com.zhy.smail.serial;

import gnu.io.CommPortIdentifier;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by wenliz on 2017/1/20.
 */
public class SerialPortInfo {
    public static List<String> getPortList(){
        List<String> results = new ArrayList<String>();
        Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers();
        CommPortIdentifier portId;
        while(portList.hasMoreElements()){
            portId = (CommPortIdentifier)portList.nextElement();
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL){
                results.add(portId.getName());
            }
        }
        return results;
    }

    private String portName;
    private Integer baudRate;
    private Integer dataBits;
    private Integer stopBits;
    private Integer parity;
    private Integer pollingEnable;
    private Integer pollingPeriod;


    public Integer getPollingEnable() {
        return pollingEnable;
    }

    public void setPollingEnable(Integer pollingEnable) {
        this.pollingEnable = pollingEnable;
    }

    public Integer getPollingPeriod() {
        return pollingPeriod;
    }

    public void setPollingPeriod(Integer pollingPeriod) {
        this.pollingPeriod = pollingPeriod;
    }

    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public Integer getBaudRate() {
        return baudRate;
    }

    public void setBaudRate(Integer baudRate) {
        this.baudRate = baudRate;
    }

    public Integer getDataBits() {
        return dataBits;
    }

    public void setDataBits(Integer dataBits) {
        this.dataBits = dataBits;
    }

    public Integer getStopBits() {
        return stopBits;
    }

    public void setStopBits(Integer stopBits) {
        this.stopBits = stopBits;
    }

    public Integer getParity() {
        return parity;
    }

    public void setParity(Integer parity) {
        this.parity = parity;
    }

}
