package com.zhy.smail.serial;

import com.zhy.smail.lcp.command.LcCommand;
import com.zhy.smail.lcp.util.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wenliz on 2017/2/24.
 */
public class UdpReceiver implements Runnable {
    private static Log log = LogFactory.getLog(UdpReceiver.class);

    private DatagramSocket socket = null;
    private UdpGateway gateway;
    private volatile boolean canceled;
    private static Map<String, byte[]> cachedMsg = new HashMap<String, byte[]>();

    public UdpReceiver(UdpGateway gateway, DatagramSocket socket) {
        this.gateway = gateway;
        this.socket = socket;
//		this.cachedMsg = new HashMap<String, byte[]>();

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
                //The size of cache must be 20k at least which is as same as the size of rcu sending cache.
                byte[] data = new byte[20480];
                DatagramPacket packet = new DatagramPacket(data, data.length);
                socket.receive(packet);

                String recv = (new String(data)).trim();
                log.debug("received packet first:" + recv);


                if(data.length<3) continue;

                int startIndex = getStartIndex(data, 0);
                if(startIndex<0) continue;
                int endIndex = getEndIndex(data, 3);
                if(endIndex<3) continue;
                data = extractData(data, startIndex, endIndex);
                if (log.isDebugEnabled()) {
                    String message = "Received data：ip=" + packet.getAddress().getHostAddress() 	+ " ;portNo=" + packet.getPort()
                            + ";data=" + StringUtil.parse(packet.getData());
                    log.debug(message);
                }
                gateway.put( data);

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

    private int getStartIndex(byte[] data, int startIndex){
        for(int i=startIndex; i<data.length-1; i++){
            if (data[i] == (byte) LcCommand.START_FLAG &&  data[i+1] == (byte)LcCommand.START_RESPONSE){
                return i;
            }
        }

        return -1;
    }

    private int getEndIndex(byte[] data, int startIndex){
        for(int i=startIndex; i<data.length; i++){
            if (data[i] == (byte)LcCommand.END_FLAG ){
                return i;
            }
        }

        return -1;
    }

    private byte[] extractData(byte[] data, int first, int end) {
        byte[] result = new byte[end - first + 1];
        for (int i = 0; first <= end; i++, first++) {
            result[i] = data[first];
        }
        return result;
    }

}
