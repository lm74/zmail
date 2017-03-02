package com.zhy.smail.lcp.command;

import com.zhy.smail.lcp.BoxEntry;
import com.zhy.smail.lcp.LcResult;
import com.zhy.smail.lcp.util.Octet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenliz on 2017/1/18.
 */
public class ReadBoxStatus extends LcCommand{
    public byte[] getDataUnits(){
        byte[] data = new byte[5];
        data[0] = (byte)LcCommand.READ_BOX_STATUS;
        data[1] = (byte)2;
        int[] boxes = commandInfo.getData();
        data[2] = 0x00;
        data[3] = Octet.getFirstByte(boxes[0]);
        data[4] = Octet.getFirstByte(boxes.length);
        return data;
    }

    protected void parseUnit(byte[] data, LcResult result){
        int flag = (int)data[3]&0xFF;
        if(flag == 0){
            int startNo = (int)data[4]&0xFF;
            int number = (int)data[5]&0xFF;
            int index = 0;
            List<BoxEntry> boxList = new ArrayList<>();
            for(int i=6; i<data.length; i++){
                int boxNo = startNo + index;
                int status = data[i];
                BoxEntry entry = new BoxEntry(boxNo, status);
                index++;
                boxList.add(entry);
            }
            result.setData(boxList.toArray());
            result.setErrorNo(LcResult.SUCCESS);
        }
        else{
            result.setErrorNo(flag);
        }
    }
}
