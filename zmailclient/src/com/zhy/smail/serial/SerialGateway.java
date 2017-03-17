package com.zhy.smail.serial;

import com.zhy.smail.config.LocalConfig;
import com.zhy.smail.lcp.util.StringUtil;
import gnu.io.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.Log4JLogger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by wenliz on 2017/1/20.
 */
public class SerialGateway extends  Gateway{
    private static Log log = LogFactory.getLog(SerialGateway.class);


    public static BlockingQueue<byte[]> received = new LinkedBlockingQueue<byte[]>();
    private String portName;
    private Integer baudRate;
    private Integer dataBits;
    private Integer stopBits;
    private Integer parity;
    private InputStream in;
    private OutputStream out;
    private SerialPort serialPort;
    private String gatewayId;
    private boolean opened = false;

    public boolean isOpened() {
        return opened;
    }



    public SerialGateway(){
        this.gatewayId = "default";
        SerialPortInfo portInfo = LocalConfig.getInstance().getSerialPortInfo();
        if(portInfo==null){
            log.info("获取端口配置信息出错。");
            return;
        }
        this.portName = portInfo.getPortName();
        this.baudRate = portInfo.getBaudRate();
        this.stopBits = portInfo.getStopBits();
        this.dataBits = portInfo.getDataBits();
        this.parity = portInfo.getParity();
    }

    public String getPortName(){
        return portName;
    }

    public void setPortName(String portName){
        this.portName = portName;
    }




    public int getQueueSchedulingInterval(){
        return 5000;
    }
    public void put(byte[] packet){
        try{
            log.info("从串口"+portName+"收到数据：" + StringUtil.parse(packet));
            System.out.println("从串口"+portName+"收到数据：" + StringUtil.parse(packet));
            received.put(packet);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


    public boolean sendMessage(byte[] data) throws IOException {
        try {
            if (out == null) {  //端口如果没有打开，测进行打开
                String message = "Failed to send packet(%s) because the port %s cannot be opened.";
                message = String.format(message, StringUtil.parse(data), portName);
                log.error(message);
                return false;
            }
            out.write(data);

            log.info("数据包:" + StringUtil.parse(data) + " 端口:" + portName + " 发送成功. ");
            return true;
        }
        catch (IOException e){
            this.close();
            try {
                connect(portName);
            }
            catch (Exception e1){

            }
            return false;
        }

    }



    /**
     * 打开指定串口
     * @param portName 串口名称
     * @throws GatewayException
     */
    public void  connect(String portName) throws GatewayException{
        opened = false;
        try{
            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);

            if (portIdentifier.isCurrentlyOwned()){
                String message3 = "端口 " + portName +" 已经被占用.";
                throw new GatewayException(message3);
            }

            CommPort commPort =  portIdentifier.open(gatewayId, 3000);//(the owner, wait minute)
            if (commPort instanceof SerialPort){
                serialPort = (SerialPort)commPort;
                serialPort.setSerialPortParams(baudRate,dataBits,stopBits, parity);
                in = serialPort.getInputStream();
                out = serialPort.getOutputStream();

                serialPort.addEventListener(new SerialReader(this, in));

                serialPort.notifyOnDataAvailable(true);
                log.info("打开端口" + portName+"成功.");
                opened = true;
            }
            else {
                String message413="非法端口 " + portName +".打开 " + portName+"失败.";
                throw new GatewayException(message413);
            }
        }
        catch(NoSuchPortException e){
            String message2 = "端口 " + portName +" 不存在.打开 " + portName+"失败.";

            throw new GatewayException(message2);
        }
        catch(PortInUseException e){
            String message = "端口 " + portName +"已经被占用.";

            throw new GatewayException(message);
        }
        catch(Exception e){
            String message412="打开端口 "+ portName+ "失败,原因:" + e.getMessage();
            throw new GatewayException(message412);
        }
    }


    public void close() {
        if(serialPort != null){
            serialPort.close();
            opened = false;
        }
    }
}