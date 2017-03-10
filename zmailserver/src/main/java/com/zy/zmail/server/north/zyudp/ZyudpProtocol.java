package com.zy.zmail.server.north.zyudp;

import com.zy.zmail.server.north.DoorMessage;
import com.zy.zmail.server.north.zyudp.command.ZyudpCommand;

/**
 * Created by wenliz on 2017/3/7.
 */
public class ZyudpProtocol {
    private static ZyudpProtocol _instance;

    public byte[] pack(DoorMessage message){

        ZyudpCommand  command = ZyudpCommand.getInstance(message);
        return command.pack();
    }

    public static ZyudpProtocol getInstance(){
        if(_instance == null){
            _instance = new ZyudpProtocol();
        }
        return _instance;
    }

}
