package com.zy.zmail.server.north.zyudp;

import com.zy.zmail.server.north.util.StringUtil;
import com.zy.zmail.server.north.zyudp.command.ZyudpCommand;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wenliz on 2017/3/21.
 */
public class ZyudpReceiver implements Runnable {
    private static Log log = LogFactory.getLog(ZyudpReceiver.class);

    private DatagramSocket socket = null;
    private ZyudpGateway gateway;
    private volatile boolean canceled;
    private static Map<String, byte[]> cachedMsg = new HashMap<String, byte[]>();

    public ZyudpReceiver(ZyudpGateway gateway, DatagramSocket socket) {
        this.gateway = gateway;
        this.socket = socket;
        canceled = false;
    }

    public void run() {
        try {
            this.socket.setSoTimeout(5000);
        } catch (Exception e) {

        }
        receiveData();

    }

    // 接收数据包
    private void receiveData() {
        while (!canceled) {
            try {

                byte[] data = new byte[8096];
                DatagramPacket packet = new DatagramPacket(data, data.length);
                socket.receive(packet);

                data = extractPacket(data);

                String message = "Received data：ip=" + packet.getAddress().getHostAddress() 	+ " ;portNo=" + packet.getPort()
                        + ";data=" + StringUtil.parse(packet.getData());
                log.debug(message);

                gateway.put(packet.getAddress().getHostAddress(), packet.getPort(), data);

            } catch (SocketTimeoutException e) {
                continue;
            } catch (IOException e) {
                log.error("接收数据包时发生IO错误，错误信息为:" + e.getMessage());
            } catch (Exception e) {
                log.error("接收数据包时出错，错误信息为:" + e.getMessage());
            }
        }
    }

    public Boolean getCanceled() {
        return canceled;
    }

    public void setCanceled(Boolean canceled) {
        this.canceled = canceled;
    }

    public static byte[] extractPacket(byte[] data)  {
        byte startFlag1 = (byte)(ZyudpCommand.START_FLAG & 0xFF);
        byte startFlag2 = (byte)(ZyudpCommand.START_REQUEST & 0xFF);


        byte[] packet =extractPacket(startFlag1, startFlag2, data);
        return packet;
    }

    public static byte[] extractPacket(byte startFlag1, byte startFlag2, byte[] data) {
        List<Byte> packetBytes = new ArrayList<Byte>();
        boolean foundStartFlag = false;
        boolean foundEndFlag = false;
        for(int i=0; i<data.length; i++) {
            byte b = data[i];
            if (!foundStartFlag) {
                if (b == startFlag1 && data[i+1] == startFlag2) {
                    foundStartFlag = true;
                    packetBytes.add(b);
                }
            } else {
                packetBytes.add(b);
                if (b == startFlag1) {
                    foundEndFlag = true;
                    break;
                }
            }
        }

        byte[] packet = new byte[packetBytes.size()];
        for(int i = 0; i < packet.length; i++) {
            packet[i] = packetBytes.get(i);
        }

        return packet;
    }

}
