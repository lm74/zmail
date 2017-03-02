package com.zhy.smail.serial;

import com.zhy.smail.lcp.util.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by wenliz on 2017/2/24.
 */
public class UdpGateway  extends Gateway{
    private static Log log = LogFactory.getLog(UdpGateway.class);
    public static String IpAddress = "192.168.1.105";
    /**
     * UDP侦听端口号
     */
    private Integer portNo;
    private DatagramSocket socket = null;
    private Thread receiverThread;
    private boolean opened = false;
    /**
     * 数据接收对象
     */
    private UdpReceiver udpReceiver;

    public UdpGateway(Integer portNo){

        this.portNo = portNo;
    }



    public int getQueueSchedulingInterval(){
        return 5000;
    }

    public boolean isOpened() {
        return opened;
    }

    public boolean sendMessage(byte[] data) throws IOException, InterruptedException{

            try{
                InetAddress inet = InetAddress.getByName(IpAddress);

                DatagramPacket packet = new DatagramPacket(data, data.length,
                        inet, 8020);
                socket.send(packet);
                String send = new String(data);

                String message = "Send packet successful：ip=" + IpAddress + " ;portNo=" +
                        8020 + ";data=";

                 message += StringUtil.parse(data);

                log.info(message);

            }
            catch(IOException e){
                log.error("IO error while UDP Service is receiving data:" + e.getMessage());
            }
            catch(Exception e){
                log.error("Error while UDP Service is receiving data:" + e.getMessage());
            }

            return true;

    }

    public void put( byte[] packet){
        try{
            SerialGateway.received.add(packet);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


    public void startGateway() throws  GatewayException, IOException, InterruptedException{
        listenTo();
        udpReceiver = new UdpReceiver(this, socket);
        receiverThread = new Thread(udpReceiver);
        receiverThread.start();
        opened = true;

    }

    public void stopGateway() throws GatewayException, IOException, InterruptedException{
        close();
        opened = false;
    }

    public void close() {
        if(udpReceiver != null){
            udpReceiver.setCanceled(true);
        }
        if(receiverThread != null){
            try {
                receiverThread.interrupt();
                receiverThread.join();
            }
            catch (Exception e){

            }
            receiverThread= null;
        }

        if (socket != null){
            socket.close();
            socket = null;
        }
    }

    private void listenTo() throws GatewayException{
        try{
            log.debug("UDP Service is binding the port "+ portNo +"..." );
            socket = new DatagramSocket(portNo);
            socket.setReceiveBufferSize(8096);  // 256K
        }
        catch(Exception e){
            String message = "Error while listening to port:" + portNo+", error:" + e.getMessage();
            log.error(message);
            throw new GatewayException(message);
        }
        log.info("UDP Service binded the port " + portNo+" successfully.");
    }
}
