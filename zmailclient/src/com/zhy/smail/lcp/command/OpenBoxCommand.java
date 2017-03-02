package com.zhy.smail.lcp.command;

import com.zhy.smail.lcp.LcResult;

/**
 * Created by wenliz on 2017/1/18.
 */
public class OpenBoxCommand extends LcCommand{
    public byte[] getDataUnits(){
        byte[] data = new byte[4];
        data[0] = (byte)LcCommand.OPEN_BOX;
        data[1] = 0x01;
        data[2] = 0x00;
        data[3] = (byte)commandInfo.getData()[0];
        return data;
    }

    protected void parseUnit(byte[] data, LcResult result){
        int flag = (int)data[3];
        if(flag == 0){
            result.setErrorNo(LcResult.SUCCESS);
        }
        else{
            result.setErrorNo(flag);
        }
    }
}
