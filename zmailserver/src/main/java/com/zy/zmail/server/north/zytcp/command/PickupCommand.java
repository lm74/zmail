package com.zy.zmail.server.north.zytcp.command;

import com.zy.zmail.server.north.DoorResult;
import com.zy.zmail.server.north.util.Octet;

/**
 * Created by wenliz on 2017/3/7.
 */
public class PickupCommand extends ZytcpCommand {
    @Override
    protected byte[] getDataUnits(){
        byte[] data = new byte[3];
        if(message.getMailType() == 0){
            data[0] = 1;
        }
        else{
            data[0] = 2;
        }

        data[1] = Octet.getFirstByte(message.getCabinetNo());
        data[2] = Octet.getFirstByte(message.getBoxNo());
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