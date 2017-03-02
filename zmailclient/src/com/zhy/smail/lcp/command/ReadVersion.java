package com.zhy.smail.lcp.command;

import com.zhy.smail.lcp.LcResult;
import com.zhy.smail.lcp.util.StringUtil;

/**
 * Created by wenliz on 2017/1/18.
 */
public class ReadVersion extends LcCommand {
    public byte[] getDataUnits(){
        byte[] data = new byte[3];
        data[0] = LcCommand.READ_VERSION;
        data[1] = 0x00;
        data[2] = 0x00;
        return data;
    }

    protected void parseUnit(byte[] data, LcResult result){
        int flag = (int)data[2];
        if(flag == 0){
            byte[] versions = new byte[data.length-3];
            System.arraycopy(data, 3, versions, 0, versions.length);
            String version = new String(versions);
            version = StringUtil.trim(version);
            result.setVersion(version);
            result.setErrorNo(LcResult.SUCCESS);
        }
        else{
            result.setErrorNo(flag);
        }
    }
}
