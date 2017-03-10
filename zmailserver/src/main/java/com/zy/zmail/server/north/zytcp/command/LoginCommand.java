package com.zy.zmail.server.north.zytcp.command;


import com.zy.zmail.server.north.DoorResult;

/**
 * Created by wenliz on 2017/1/18.
 */
public class LoginCommand extends ZytcpCommand {
    @Override
    protected byte[] getDataUnits(){
        byte[] data = new byte[1];
        data[0] = 0x00;
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
