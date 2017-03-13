package com.zy.zmail.server.north.zytcp;

import com.zy.zmail.server.north.DoorMessage;
import com.zy.zmail.server.north.DoorResult;
import com.zy.zmail.server.north.util.StringUtil;
import com.zy.zmail.server.north.zytcp.command.ZytcpCommand;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by wenliz on 2017/3/8.
 */
public class ZytcpGateway {
    private static int WAIT_TIME = 10;
    private static Log log = LogFactory.getLog(ZytcpGateway.class);
    public static BlockingQueue<byte[]> response = new LinkedBlockingQueue<byte[]>();

    private Integer portNo;
    private String serverIp;
    private Socket socket;

    private boolean opened;
    private boolean logined;
    private long lastHeartbeat = 0;

    public boolean isOpened() {
        return opened;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    public boolean isLogined() {
        return logined;
    }

    public void setLogined(boolean logined) {
        this.logined = logined;
    }

    public ZytcpGateway(String serverIp, Integer portNo){
        logined = false;

        opened = false;
        this.serverIp = serverIp;
        this.portNo = portNo;
    }

    public void close(){
        if(socket == null) return;

        try {
            socket.close();
            opened = false;
        }
        catch (IOException e){

        }
    }



    public boolean heartbeat(Integer buildingNo, Integer unitNo){

        if((System.currentTimeMillis() - lastHeartbeat)<90*1000){
            return true;
        }
        DoorMessage message = new DoorMessage();
        message.setCommandNo(ZytcpCommand.HEARTBEAT);
        message.setBuildingNo(buildingNo);
        message.setUnitNo(unitNo);
        ZytcpProtocol protocol = ZytcpProtocol.getInstance();
        byte[] data = protocol.pack(message);

        if(!sendMessage(data)) return false;

        try {
            byte[] sdata = response.poll(WAIT_TIME, TimeUnit.SECONDS);
            DoorResult result = protocol.parse(sdata);
            if(result.getErrorNo() == DoorResult.SUCCESS){

            }
        }
        catch (InterruptedException e){

        }
        lastHeartbeat = System.currentTimeMillis();
        return true;
    }

    public boolean login(Integer buildingNo, Integer unitNo){
        logined = false;
        DoorMessage message = new DoorMessage();
        message.setCommandNo(ZytcpCommand.LOGIN);
        message.setBuildingNo(buildingNo);
        message.setUnitNo(unitNo);
        ZytcpProtocol protocol = ZytcpProtocol.getInstance();
        byte[] data = protocol.pack(message);

        if(!sendMessage(data)) return false;

        try {
            byte[] sdata = response.poll(WAIT_TIME, TimeUnit.SECONDS);
            if(sdata == null){
                return false;
            }
            DoorResult result = protocol.parse(sdata);
            if(result.getErrorNo() == DoorResult.SUCCESS){
                logined = true;
            }
        }
        catch (InterruptedException e){

        }
        return logined;
    }

    public boolean sendMessage(byte[] data){
        try {
            socket.getOutputStream().write(data);
            String message = "发送数据包成功：ip=" + serverIp + " ;portNo=" +portNo
                    + ";data=" + StringUtil.parse(data);;

           log.info(message);
            return true;
        }
        catch (IOException e){
            opened = false;
            logined = false;
            return false;
        }
    }

    public static void receive(byte[] data){
        try {
            response.put(data);
            String message = "收到数据包：data=" + StringUtil.parse(data);;

            log.info(message);
        }
        catch (InterruptedException e){

        }
    }



    public boolean open(){
        if(opened){
            close();
        }
        try{
            InetAddress inet = InetAddress.getByName(serverIp);
            socket = new Socket(inet, portNo);
            new Thread(new ZytcpReceiver(socket));
            opened = true;
        }
        catch (UnknownHostException e){
            log.error("门禁服务器（IP:" + serverIp+")不存在!");
        }
        catch (IOException e){
            log.error("建立门禁服务器(IP:" + serverIp+")的连接时出错:" + e.getMessage());
        }
        return opened;

    }
}
