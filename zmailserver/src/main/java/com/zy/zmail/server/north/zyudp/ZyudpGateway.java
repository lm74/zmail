package com.zy.zmail.server.north.zyudp;

import com.zy.zmail.server.north.util.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by wenliz on 2017/3/21.
 */
public class ZyudpGateway {
    private static Log  log = LogFactory.getLog(ZyudpGateway.class);
    private static ZyudpGateway _instance = null;

    public static BlockingQueue<byte[]> response = new LinkedBlockingQueue<byte[]>();
    /**
     * UDP侦听端口号
     */
    private Integer portNo;
    private DatagramSocket socket = null;
    private String serverIp;

    private Thread receiverThread;
    /**
     * 数据接收对象
     */
    private ZyudpReceiver udpReceiver;
    private boolean started;

    public static ZyudpGateway getInstance(String serverIp, Integer portNo){
        if(_instance == null){
           _instance = new ZyudpGateway(serverIp, portNo);
            _instance.startGateway();
        }
        else {
            if(!_instance.serverIp.equals(serverIp) || !_instance.portNo.equals(portNo)){
                if(_instance.started) {
                    _instance.stopGateway();
                }
                _instance.serverIp = serverIp;
                _instance.portNo = portNo;
                _instance.startGateway();
            }
        }
        return _instance;
    }

    public ZyudpGateway(String serverIp, Integer portNo){
        this.serverIp = serverIp;
        this.portNo = portNo;
        started = false;
    }


    public boolean sendMessage(byte[] data) {
        try{
            if(!started){
                startGateway();
            }

            InetAddress inet = InetAddress.getByName(this.serverIp);
            DatagramPacket packet = new DatagramPacket(data, data.length, inet, portNo);
            socket.send(packet);
            String message = "Send packet successful：ip=" + serverIp+ " ;portNo=" +
                    portNo + ";data="+ StringUtil.parse(data);
            log.info(message);

            byte[] responseData = response.poll(3, TimeUnit.SECONDS);
            if(responseData == null){
                log.info("没有数据包收接到，发包失败。");
                return false;
            }
            return true;
        }
        catch(IOException e){
            log.error("IO error while UDP Service is sending data:" + e.getMessage());
        }
        catch(Exception e){
            log.error("Error while UDP Service is sending data:" + e.getMessage());
        }

        return false;
    }

    public void put(String ipAddress, Integer portNo, byte[] packet){
        try{
            log.debug("Received data from "+ipAddress+"：" + StringUtil.parse(packet));
            response.add(packet);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


    public boolean startGateway() {
        listenTo();
        if(started) {
            udpReceiver = new ZyudpReceiver(this, socket);
            receiverThread = new Thread(udpReceiver);
            receiverThread.start();
            return true;
        }

        return false;
    }

    public void stopGateway(){
        close();

    }

    private void close() {
        if(udpReceiver != null){
            udpReceiver.setCanceled(true);
        }
        if(receiverThread != null){
            receiverThread.interrupt();
            try {
                receiverThread.join();
            }
            catch (InterruptedException e){

            }
            receiverThread= null;
        }

        if (socket != null){
            socket.close();
            socket = null;
        }
    }

    private void listenTo() {
        try{
            log.debug("UDP Service is binding the port "+ portNo +"..." );
            socket = new DatagramSocket(portNo);
            socket.setReceiveBufferSize(8096);  // 256K
            log.info("UDP Service binded the port " + portNo+" successfully.");
            started = true;
        }
        catch(Exception e){
            String message = "Error while listening to port:" + portNo+", error:" + e.getMessage();
            log.error(message);
            started = false;
        }

    }
}
