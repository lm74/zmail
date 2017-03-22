package com.zy.zmail.server.north.zyudp.command;

import com.zy.zmail.server.north.DoorResult;
import com.zy.zmail.server.north.util.Octet;
import com.zy.zmail.server.north.zytcp.command.ZytcpCommand;

/**
 * Created by wenliz on 2017/3/7.
 */
public class QueryStatusCommand extends ZyudpCommand {
    @Override
    protected byte[] getDataUnits(){
        byte[] value = (byte[])message.getData();

        return value;
    }

    protected void adjust(byte[] data){

    }

    protected void parseUnit(byte[] data, DoorResult result){
        int flag = (int)data[0];
        result.setErrorNo(flag);
        result.setCommandNo(ZyudpCommand.QUERY_STATUS);

    }
}
