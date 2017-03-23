package com.zy.zmail.server.north.zyudp;

import com.zy.zmail.server.north.DoorMessage;
import com.zy.zmail.server.north.DoorResult;
import com.zy.zmail.server.north.zytcp.command.ZytcpCommand;
import com.zy.zmail.server.north.zyudp.command.ZyudpCommand;

/**
 * Created by wenliz on 2017/3/7.
 */
public class ZyudpProtocol {
    private static ZyudpProtocol _instance;

    public byte[] pack(DoorMessage message){
        switch (message.getCommandNo()){
            case ZytcpCommand.DELIVERY:
                message.setCommandNo(ZyudpCommand.DELIVERY);
                break;
            case ZytcpCommand.PICKUP:
                message.setCommandNo(ZyudpCommand.PICKUP);
                break;
        }

        ZyudpCommand  command = ZyudpCommand.getInstance(message.getCommandNo());
        return command.pack(message);
    }

    public DoorResult parse(byte[] packet){
        int commandNo = ZyudpCommand.getCommandNo(packet);
        ZyudpCommand command = ZyudpCommand.getInstance(commandNo);
        return command.parse(packet);
    }

    public static ZyudpProtocol getInstance(){
        if(_instance == null){
            _instance = new ZyudpProtocol();
        }
        return _instance;
    }

}
