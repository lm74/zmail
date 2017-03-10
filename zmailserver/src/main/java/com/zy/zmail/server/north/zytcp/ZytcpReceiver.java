package com.zy.zmail.server.north.zytcp;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by wenliz on 2017/3/8.
 */
public class ZytcpReceiver implements Runnable {
    private boolean canceled;
    private Socket socket;

    public ZytcpReceiver(Socket socket){
        this.socket = socket;
    }
    public void run(){
        while (!canceled){
            byte[] data = new byte[8096];
            try {
                int len = socket.getInputStream().read(data);
                if(len>0) {
                    byte[] other = new byte[len];
                    System.arraycopy(data, 0, other, 0, other.length);
                    ZytcpGateway.receive(other);
                }

            }
            catch (IOException e){

            }
            try{
                Thread.sleep(100);
            }
            catch (Exception e){

            }


        }
    }
}
