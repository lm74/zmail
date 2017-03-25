package com.zy.zmail.server.north;

import com.zy.zmail.server.cabinet.entity.CabinetInfo;
import com.zy.zmail.server.cabinet.service.CabinetService;
import com.zy.zmail.server.delivery.entity.DeliveryLog;
import com.zy.zmail.server.delivery.service.DeliveryLogService;
import com.zy.zmail.server.north.Abudp.AbudpSender;
import com.zy.zmail.server.north.util.Octet;
import com.zy.zmail.server.north.util.StringUtil;
import com.zy.zmail.server.north.zytcp.ZytcpGateway;
import com.zy.zmail.server.north.zytcp.ZytcpProtocol;
import com.zy.zmail.server.north.zytcp.command.ZytcpCommand;
import com.zy.zmail.server.north.zyudp.ZyudpGateway;
import com.zy.zmail.server.north.zyudp.ZyudpProtocol;
import com.zy.zmail.server.north.zyudp.command.ZyudpCommand;
import com.zy.zmail.server.user.entity.UserInfo;
import com.zy.zmail.server.user.service.UserService;
import org.springframework.stereotype.Service;

import javax.swing.plaf.metal.OceanTheme;
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
    private CabinetService cabinetService;
    private int waitTime = 200;
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

        //restart();
    }
    public void setUnitNo(int unitNo){
        connection.setUnitNo(unitNo);
        //restart();
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

        //restart();
    }
    private void restart(){
        if(abudpSender !=null){
            abudpSender.close();
            abudpSender = null;
        }
        if(zytcpGateway!=null){
            zytcpGateway.close();
            zytcpGateway = null;
        }
        if(zyudpGateway != null){
            zyudpGateway.stopGateway();
            zyudpGateway = null;
        }
    }

    private DoorConnection connection;
    private AbudpSender abudpSender =null;
    private ZyudpGateway zyudpGateway = null;
    private ZytcpGateway zytcpGateway = null;

    public DoorSystemRunner(DoorConnection connection, UserService userService, DeliveryLogService deliveryLogService,
                            CabinetService cabinetService){
        this.userService = userService;
        this.deliveryLogService = deliveryLogService;
        this.cabinetService = cabinetService;
        canceled = false;
        connected = false;
        this.connection  = connection;
    }


    public void run(){
        while (!canceled){
            try {
                switch (connection.getProtocolType().intValue()) {
                    case 0:
                        processAbUdp();
                        break;
                    case 1:
                        processTcp();
                        break;
                    case 2:
                        processZyUdp();
                        break;
                    default:
                        sleep(100);
                }
            }
            catch (Exception e){

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
            DoorMessage message = messages.poll(waitTime, TimeUnit.MILLISECONDS);
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

            waitTime = 200;
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
            byte[] data = ZytcpGateway.response.poll(1, TimeUnit.SECONDS);
            if(data != null){
                ZytcpProtocol protocol = ZytcpProtocol.getInstance();
                DoorResult result= protocol.parse(data);
                if(result.getCommandNo() == ZytcpCommand.QUERY_STATUS){
                    byte flag = 0;
                    String userName = "";
                    if(result.getBuildingNo()>0){
                        userName+=result.getBuildingNo();
                    }
                    else{
                        flag = 2;
                    }
                    if(result.getUnitNo()>0){
                        userName += result.getUnitNo();
                    }
                    else{
                        flag = 2;
                    }
                    if(result.getFloorNo()>0){
                        userName += result.getFloorNo();
                    }
                    else{
                        flag = 2;
                    }
                    if(result.getRoomNo()>0){
                        if(result.getRoomNo()>=10){
                            userName += result.getRoomNo();
                        }
                        else{
                            userName += "0" + result.getRoomNo();
                        }
                    }
                    else{
                        flag = 2;
                    }
                    byte[] context;
                    if(flag == 0) {
                        context = getFlag(userName);
                    }
                    else{
                        context = new byte[1];
                        context[0]= flag;
                    }
                    DoorMessage message = new DoorMessage();
                    message.setBuildingNo(result.getBuildingNo());
                    message.setUnitNo(result.getUnitNo());
                    message.setFloorNo(result.getFloorNo());
                    message.setRoomNo(result.getRoomNo());
                    message.setData(context);
                    message.setCommandNo(ZytcpCommand.QUERY_STATUS_RESPONSE);
                    byte[] rdata = protocol.pack(message);
                    zytcpGateway.sendMessage(rdata);
                }
            }
        }
        catch (InterruptedException e){

        }
    }

    private byte[] getFlag(String userName) {
        byte flag=0;
        String cabinetNo="";
        int cabinetId=0;
        int boxNo = 0;
        byte[] data = new byte[1];
        data[0] = 0;
        UserInfo user = userService.getByUserName(userName);
        if(user != null) {
            List<DeliveryLog> deliveryLogList = deliveryLogService.listByOwner(user.getUserId());
            if(deliveryLogList.size() == 0){
                data[0] = 5;
            }
            else {
                boolean hasMail = false;
                boolean hasPacket = false;
                for (int i = 0; i < deliveryLogList.size(); i++) {
                    DeliveryLog log = deliveryLogList.get(i);
                    if (log.getDeliveryType() == 0) {
                        boxNo = log.getBoxInfo().getSequence();
                        cabinetId = log.getBoxInfo().getCabinetId();
                        hasMail = true;
                    } else if (log.getDeliveryType() == 1) {
                        boxNo = log.getBoxInfo().getSequence();
                        cabinetId = log.getBoxInfo().getCabinetId();
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
                if(hasMail || hasPacket){
                    data = new byte[3];
                    data[0] = flag;

                    CabinetInfo cabinetInfo = cabinetService.getByCabinetId(cabinetId);
                    data[1] = Octet.getFirstByte(Integer.parseInt(cabinetInfo.getCabinetNo()));
                    data[2] = Octet.getFirstByte(boxNo);
                }
            }
        }
        else {
            data[0] = 2;
        }
        return data;
    }

    private void processAbUdp(){
        try {
            DoorMessage message = messages.take();
            ZyudpProtocol protocol = ZyudpProtocol.getInstance();
            byte[] data = protocol.pack(message);
            if(data == null || data.length==0) return;

            if(abudpSender == null) {
                abudpSender = new AbudpSender(connection.getServerPort());
            }
            abudpSender.send(data, connection.getServerIp(), connection.getServerPort());

        }
        catch (InterruptedException e){
        }
    }

    private void processZyUdp(){
        try {
            DoorMessage message = messages.poll(2, TimeUnit.SECONDS);

            if(connection.getServerIp()==null || connection.getServerIp().length()==0) return;
            if(connection.getServerPort()<1024) return;

            if (zyudpGateway == null) {
                zyudpGateway = ZyudpGateway.getInstance(connection.getServerIp(), connection.getServerPort());
            }
            if(message == null){
                reponseZyudp();
                return;
            }

            ZyudpProtocol protocol = ZyudpProtocol.getInstance();
            byte[] data = protocol.pack(message);
            if(data == null || data.length==0) return;

            zyudpGateway.sendMessage(data);

        }
        catch (InterruptedException e){

        }
    }

    private void reponseZyudp(){
        try {
            byte[] data = ZyudpGateway.response.poll(1, TimeUnit.SECONDS);
            if(data != null){
                ZyudpProtocol protocol = ZyudpProtocol.getInstance();
                DoorResult result= protocol.parse(data);
                if(result.getCommandNo() == ZyudpCommand.QUERY_STATUS){

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
                    //byte[] context = getFlag(userName);
                    DoorMessage message = new DoorMessage();
                    message.setSectionNo(result.getSectionNo());
                    message.setBuildingNo(result.getBuildingNo());
                    message.setUnitNo(result.getUnitNo());
                    message.setFloorNo(result.getFloorNo());
                    message.setRoomNo(result.getRoomNo());
                    message.setCommandNo(ZyudpCommand.QUERY_STATUS_RESPONSE);
                    message.setData(getMailInfo(userName));
                    byte[] rdata = protocol.pack(message);
                    zyudpGateway.sendMessage(rdata);
                }
            }
        }
        catch (InterruptedException e){

        }
    }
    private byte[] emptyMail() {
        byte[] data = new byte[1];
        data[0] = 0x00;
        return data;
    }

    private byte[] getMailInfo(String userName){
        UserInfo user = userService.getByUserName(userName);
        if(user == null){
            return emptyMail();
        }

        List<DeliveryLog> deliveryLogList = deliveryLogService.listByOwner(user.getUserId());
        if(deliveryLogList.size() == 0){
            return emptyMail();
        }

        int length = deliveryLogList.size();
        if(length>20){
            length = 20;
        }
        byte[] data= new byte[length*2+2];
        data[0] = (byte)(length);

        boolean hasMail = false;
        boolean hasPacket = false;
        for (int i = 0; i < length; i++) {
            DeliveryLog log = deliveryLogList.get(i);
            CabinetInfo cabinetInfo = cabinetService.getByCabinetId(log.getBoxInfo().getCabinetId());
            data[i*2+2] =  (byte)(StringUtil.getInteger(cabinetInfo.getCabinetNo())&0xFF);
            data[i*2+1+2] =  (byte)(log.getBoxInfo().getSequence()&0xFF);
            if(log.getDeliveryType().equals(0)){
                hasMail = true;
            }
            else if(log.getDeliveryType().equals(1)){
                hasPacket = true;
            }
        }
        if(hasMail && hasPacket){
            data[1] = 3;
        }
        else if(hasPacket){
            data[1] = 2;
        }
        else if(hasMail){
            data[1] = 1;
        }
        else{
            data[1]=0;
        }

        return data;
    }

    private void sleep(int seconds){
        try{
            Thread.sleep( seconds );
        }
        catch (Exception e){

        }
    }

    private void connect(DoorMessage message){

    }

}
