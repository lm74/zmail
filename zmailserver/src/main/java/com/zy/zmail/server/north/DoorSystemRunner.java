package com.zy.zmail.server.north;

import com.zy.zmail.server.delivery.entity.DeliveryLog;
import com.zy.zmail.server.delivery.service.DeliveryLogService;
import com.zy.zmail.server.north.zytcp.ZytcpGateway;
import com.zy.zmail.server.north.zytcp.ZytcpProtocol;
import com.zy.zmail.server.north.zytcp.command.ZytcpCommand;
import com.zy.zmail.server.north.zyudp.ZyudpProtocol;
import com.zy.zmail.server.north.zyudp.ZyudpSender;
import com.zy.zmail.server.user.entity.UserInfo;
import com.zy.zmail.server.user.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * Created by wenliz on 2017/3/8.
 */
@Service("doorSystemRunner")
public class DoorSystemRunner implements Runnable {
    public static BlockingDeque<DoorMessage> messages = new LinkedBlockingDeque<>();
    private boolean canceled;
    private boolean connected;
    private UserService userService;
    private DeliveryLogService deliveryLogService;
    private int waitTime = 2000;
    public DoorSystemRunner(){
        canceled = false;
    }

    public DoorConnection getConnection() {
        return connection;
    }

    public void setConnection(DoorConnection connection) {
        this.connection = connection;
    }

    public void setBuildingNo(int buildingNo){
        connection.setBuildingNo(buildingNo);

        restart();
    }
    public void setUnitNo(int unitNo){
        connection.setUnitNo(unitNo);
        restart();
    }
    public void setServerIp(String serverIp){
        connection.setServerIp(serverIp);
        restart();
    }
    public void setServerPort(Integer serverPort){
        connection.setServerPort(serverPort);
        restart();
    }
    public void setProtocolType(Integer protocolType){
        connection.setProtocolType(protocolType);

        restart();
    }
    private void restart(){
        if(udpSender!=null){
            udpSender.close();
        }
        if(zytcpGateway!=null){
            zytcpGateway.close();
        }
    }

    private DoorConnection connection;
    private ZyudpSender udpSender =null;
    private ZytcpGateway zytcpGateway = null;

    public DoorSystemRunner(DoorConnection connection, UserService userService, DeliveryLogService deliveryLogService){
        this.userService = userService;
        this.deliveryLogService = deliveryLogService;
        canceled = false;
        connected = false;
        this.connection  = connection;
    }


    public void run(){
        while (!canceled){
            if(connection.getProtocolType() == 0){
                processUdp();
            }
            else if(connection.getProtocolType() == 1){
                processTcp();
            }
            else{
                sleep(1);
            }

        }
    }

    private void increaseWaitTime(){
        if(waitTime<30000){
            waitTime += 10000;
        }
    }

    private void processTcp(){
        try {
            DoorMessage message = messages.poll(waitTime, TimeUnit.SECONDS);
            if(connection.getServerIp()==null || connection.getServerIp().length()==0) return;
            if(connection.getServerPort()<1024) return;

            if (zytcpGateway == null) {
                zytcpGateway = new ZytcpGateway(connection.getServerIp(), connection.getServerPort());
                zytcpGateway.open();
            }
            if (!zytcpGateway.isOpened()) {
                if (!zytcpGateway.open()) {
                    increaseWaitTime();
                    return;
                }
            }

            if (!zytcpGateway.isLogined()) {
                if (!zytcpGateway.login(connection.getBuildingNo(), connection.getUnitNo())) {
                    increaseWaitTime();
                    return;
                }
            }

            waitTime = 2000;
            if(message == null){
                reponseQuery();
                zytcpGateway.heartbeat(connection.getBuildingNo(), connection.getUnitNo());
            }
            else {
                ZytcpProtocol protocol = ZytcpProtocol.getInstance();
                byte[] data = protocol.pack(message);
                if (data != null && data.length > 0) {

                    zytcpGateway.sendMessage(data);
                }
            }

        }
        catch (InterruptedException e){

        }
    }

    private void reponseQuery(){
        try {
            byte[] data = ZytcpGateway.response.poll(1000, TimeUnit.SECONDS);
            if(data != null){
                ZytcpProtocol protocol = ZytcpProtocol.getInstance();
                DoorResult result= protocol.parse(data);
                if(result.getCommandNo() == ZytcpCommand.QUERY_STATUS){

                    String userName = "";
                    if(result.getBuildingNo()>0){
                        userName+=result.getBuildingNo();
                    }
                    if(result.getUnitNo()>0){
                        userName += result.getUnitNo();
                    }
                    if(result.getFloorNo()>0){
                        userName += result.getFloorNo();
                    }
                    if(result.getRoomNo()>0){
                        userName += result.getRoomNo();
                    }
                    int flag = getFlag(userName);
                    DoorMessage message = new DoorMessage();
                    message.setBuildingNo(result.getBuildingNo());
                    message.setUnitNo(result.getUnitNo());
                    message.setFloorNo(result.getFloorNo());
                    message.setRoomNo(result.getRoomNo());
                    message.setCommandNo(ZytcpCommand.QUERY_STATUS_RESPONSE);
                    byte[] rdata = protocol.pack(message);
                    zytcpGateway.sendMessage(rdata);
                }
            }
        }
        catch (InterruptedException e){

        }
    }

    private int getFlag(String userName) {
        int flag=0;
        UserInfo user = userService.getByUserName(userName);
        if(user != null) {
            List<DeliveryLog> deliveryLogList = deliveryLogService.listByOwner(user.getUserId());
            if(deliveryLogList.size() == 0){
                flag = 5;
            }
            else {
                boolean hasMail = false;
                boolean hasPacket = false;
                for (int i = 0; i < deliveryLogList.size(); i++) {
                    DeliveryLog log = deliveryLogList.get(i);
                    if (log.getDeliveryType() == 0) {
                        hasMail = true;
                    } else if (log.getDeliveryType() == 1) {
                        hasPacket = true;
                    }
                }

                if (hasMail && hasPacket) {
                    flag = 1;
                } else if (hasMail) {
                    flag = 6;
                } else if (hasPacket) {
                    flag = 7;
                }
            }
        }
        else {
            flag = 2;
        }
        return flag;
    }

    private void processUdp(){
        try {
            DoorMessage message = messages.take();
            ZyudpProtocol protocol = ZyudpProtocol.getInstance();
            byte[] data = protocol.pack(message);
            if(data == null || data.length==0) return;

            if(udpSender == null) {
                udpSender = new ZyudpSender(connection.getServerPort());
            }
            udpSender.send(data, connection.getServerIp(), connection.getServerPort());
//            sleep(1);
//            udpSender.send(data, connection.getServerIp(), connection.getServerPort());
//            sleep(1);
//            udpSender.send(data, connection.getServerIp(), connection.getServerPort());
//            sleep(1);
        }
        catch (InterruptedException e){

        }
    }

    private void sleep(int seconds){
        try{
            Thread.sleep( seconds * 1000);
        }
        catch (Exception e){

        }
    }

    private void connect(DoorMessage message){

    }

}
