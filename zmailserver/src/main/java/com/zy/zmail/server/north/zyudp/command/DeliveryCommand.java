package com.zy.zmail.server.north.zyudp.command;

import com.zy.zmail.server.north.DoorResult;
import com.zy.zmail.server.north.util.Octet;
import com.zy.zmail.server.north.zytcp.command.ZytcpCommand;

/**
 * Created by wenliz on 2017/3/7.
 */
public class DeliveryCommand  extends ZyudpCommand {
    @Override
    protected byte[] getDataUnits(){
        byte[] data = new byte[9];

        data[0] = Octet.getFirstByte(message.getOperateType());
        data[1] = Octet.getFirstByte(message.getCabinetNo());
        data[2] = Octet.getFirstByte(message.getBoxNo());
        getDeliveryTime(data, 3);
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
