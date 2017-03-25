package com.zy.zmail.server.north.zytcp;

import com.zy.zmail.server.north.util.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by wenliz on 2017/3/8.
 */
public class ZytcpReceiver implements Runnable {
    private static Log log = LogFactory.getLog(ZytcpReceiver.class);

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    private boolean canceled;
    private Socket socket;
    private ZytcpGateway gateway;

    public ZytcpReceiver(Socket socket, ZytcpGateway gateway){
        this.socket = socket;
        this.gateway = gateway;
    }
    public void run(){
        while (!canceled){
            byte[] data = new byte[8096];
            try {
                int len = socket.getInputStream().read(data);
                if(len>0) {
                    byte[] other = new byte[len];
                    System.arraycopy(data, 0, other, 0, other.length);
                    String message = "收到数据包：data=" + StringUtil.parse(other);;
                    log.info(message);
                    ZytcpGateway.receive(other);
                }

            }
            catch (SocketException e){
                canceled = true;
                gateway.close();
            }
            catch (IOException e){
                e.printStackTrace();
            }
            try{
                Thread.sleep(100);
            }
            catch (Exception e){

            }


        }
    }
}
