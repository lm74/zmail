package com.zy.zmail.server.north.Abudp;

import com.zy.zmail.server.north.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by wenliz on 2017/3/8.
 */
public class AbudpSender {
    private static final Logger log = LoggerFactory.getLogger(AbudpSender.class);

    private DatagramSocket socket = null;
    private Integer portNo;
    private boolean opened;
    public AbudpSender(Integer portNo){
        this.portNo = portNo;
        opened = false;
    }

    public boolean send(byte[] data, String serverIp, Integer portNo){
        if(!opened){
            open();
        }
        try {
            InetAddress inet = InetAddress.getByName(serverIp);
            DatagramPacket packet = new DatagramPacket(data, data.length, inet, portNo);
            socket.send(packet);
            String message = "发送数据包成功：ip=" + serverIp + " ;portNo=" +portNo
                    + ";data=";

              message += StringUtil.parse(data);
            log.info(message);
            return true;
        }
        catch (UnknownHostException e){
            log.error("Error:不存在IP为" + serverIp+"的门禁服务器。");
        }
        catch (IOException e){
            log.error("发送数据包失败:ip=" + serverIp + " ;portNo=" +portNo+";" + e.getMessage());
        }
        return false;
    }


    public boolean close(){
        if(opened){
            socket.close();
            opened = false;
        }
        return opened;
    }

    public boolean open()  {
        close();

        try {
            log.info("UDP Service is binding the port " + portNo + "...");
            socket = new DatagramSocket(portNo);
            socket.setReceiveBufferSize(8096); // 4K
            opened = true;
            log.info("UDP Service binded the port " + portNo + " successfully.");
        } catch (Exception e) {
            String message = "Error while listening to port:" + portNo + ", error:" + e.getMessage();
            log.error(message);
            opened = false;
        }
        return opened;
    }
}
