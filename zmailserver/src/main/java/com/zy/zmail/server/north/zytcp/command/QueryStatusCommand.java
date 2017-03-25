package com.zy.zmail.server.north.zytcp.command;

import com.zy.zmail.server.north.DoorResult;
import com.zy.zmail.server.north.util.Octet;

/**
 * Created by wenliz on 2017/3/7.
 */
public class QueryStatusCommand extends ZytcpCommand {
    @Override
    protected byte[] getDataUnits(){
        byte[] data = (byte[]) message.getData();
        return data;
    }

    protected void adjust(byte[] data){

    }

    protected void parseUnit(byte[] data, DoorResult result){
        int flag = (int)data[0];
        result.setErrorNo(flag);
        result.setCommandNo(ZytcpCommand.QUERY_STATUS);

    }
}
