package com.zy.zmail.server.north.Abudp;

import com.zy.zmail.server.north.Abudp.command.AbudpCommand;
import com.zy.zmail.server.north.DoorMessage;

/**
 * Created by wenliz on 2017/3/7.
 */
public class AbudpProtocol {
    private static AbudpProtocol _instance;

    public byte[] pack(DoorMessage message){

        AbudpCommand command = AbudpCommand.getInstance(message);
        return command.pack();
    }

    public static AbudpProtocol getInstance(){
        if(_instance == null){
            _instance = new AbudpProtocol();
        }
        return _instance;
    }

}
