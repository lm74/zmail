package com.zhy.smail.lcp.command;

import com.zhy.smail.lcp.LcResult;
import com.zhy.smail.lcp.util.Octet;
import com.zhy.smail.lcp.util.StringUtil;

/**
 * Created by wenliz on 2017/1/18.
 */
public class ReadBoardNo  extends LcCommand {
    public byte[] getDataUnits(){
        byte[] data = new byte[3];
        data[0] = LcCommand.READ_BOARDNO;
        data[1] = 0x00;
        data[2] = 0x00;
        return data;
    }

    protected void parseUnit(byte[] data, LcResult result){
        int flag = (int)data[2];
        if(flag == 0){
            byte[] boardNoes = new byte[4];
            System.arraycopy(data, 3, boardNoes, 0, boardNoes.length);
            result.setBoardNo(Octet.toByte(boardNoes));
            result.setErrorNo(LcResult.SUCCESS);
        }
        else{
            result.setErrorNo(flag);
        }
    }
}
