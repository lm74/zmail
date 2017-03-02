package com.zhy.smail.task;

import com.zhy.smail.lcp.LcProtocol;
import com.zhy.smail.lcp.ProtocolException;
import com.zhy.smail.serial.Gateway;
import com.zhy.smail.serial.SerialGateway;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by wenliz on 2017/1/20.
 */
public class SendManager {
    public static Stage stage;

    public static Gateway gateway;

    public static boolean packAndSend(int commandNo, int boardNo, int[] boxList) throws ProtocolException {
        ResponseManager.response.clear();

        try {
            LcProtocol protocol = new LcProtocol();
            byte[] packet = protocol.pack(commandNo, boardNo, boxList);
            gateway.sendMessage(packet);


            ResponseManager.response.poll(5, TimeUnit.SECONDS);

            return true;
        }
        /*catch(ProtocolException e){
            throw new ProtocolException("打包失败:" + e.getErrorMessage());
        }*/
        catch (InterruptedException e){
            throw new ProtocolException("设备没有响应，请确认设备是否运行正常.");
        }
        catch (IOException e){
            throw new ProtocolException("发送数据包失败:" + e.getMessage());
        }
    }
}

