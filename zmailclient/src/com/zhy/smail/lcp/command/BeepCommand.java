package com.zhy.smail.lcp.command;

import com.zhy.smail.lcp.LcResult;
import com.zhy.smail.lcp.util.Octet;

/**
 * Created by wenliz on 2017/1/18.
 */
public class BeepCommand  extends LcCommand {
    public byte[] getDataUnits(){
        byte[] data = new byte[3];
        data[0] = LcCommand.BEEP;
        data[1] = 0x04;
        data[2] = 0x00;
        data[3] = Octet.getSecondByte(commandInfo.getData()[0]);
        data[4] = Octet.getFirstByte(commandInfo.getData()[0]);
        return data;
    }

    protected void parseUnit(byte[] data, LcResult result){
        int flag = (int)data[2];
        if(flag == 0){
            result.setErrorNo(LcResult.SUCCESS);
        }
        else{
            result.setErrorNo(flag);
        }
    }
}
