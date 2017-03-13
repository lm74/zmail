package com.zy.zmail.server.north.zytcp;

import com.zy.zmail.server.north.DoorMessage;
import com.zy.zmail.server.north.DoorResult;
import com.zy.zmail.server.north.zytcp.command.ZytcpCommand;

/**
 * Created by wenliz on 2017/3/7.
 */
public class ZytcpProtocol {
    private static ZytcpProtocol _instance;

    public byte[] pack(DoorMessage message){
        ZytcpCommand command = ZytcpCommand.getInstance(message.getCommandNo());
        if(command == null){
            return null;
        }
        return command.pack(message);
    }


    public DoorResult parse(byte[] packet){
        int commandNo = ZytcpCommand.getCommandNo(packet);
        ZytcpCommand command = ZytcpCommand.getInstance(commandNo);
        return command.parse(packet);
    }

    public static ZytcpProtocol getInstance(){
        if(_instance == null){
            _instance = new ZytcpProtocol();
        }
        return _instance;
    }

}
