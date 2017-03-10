package com.zy.zmail.server.north.zytcp.command;

import com.zy.zmail.server.north.DoorResult;
import com.zy.zmail.server.north.util.Octet;

/**
 * Created by wenliz on 2017/3/7.
 */
public class DeliveryCommand  extends ZytcpCommand {
    @Override
    protected byte[] getDataUnits(){
        byte[] data = new byte[1];
        Integer value = (Integer)message.getData();
        if(value == null){
            value = 0;
        }

        data[0] = Octet.getFirstByte(value);
        return data;
    }

    protected void adjust(byte[] data){

    }

    protected void parseUnit(byte[] data, DoorResult result){
        int flag = (int)data[0];
        if(flag == 1){
            result.setErrorNo(DoorResult.SUCCESS);
        }
        else{
            result.setErrorNo(flag);
        }
    }
}
