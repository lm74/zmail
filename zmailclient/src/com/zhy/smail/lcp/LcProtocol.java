package com.zhy.smail.lcp;

import com.zhy.smail.lcp.command.CommandInfo;
import com.zhy.smail.lcp.command.LcCommand;

/**
 * Created by wenliz on 2017/1/18.
 */
public class LcProtocol {
    public byte[] pack(int commandNo, int boardNo, int[] data){
        CommandInfo info = new CommandInfo();
        info.setBoardNo(boardNo);
        info.setCommandNo(commandNo);
        info.setData(data);
        LcCommand command = LcCommand.getInstance(commandNo);
        if(command == null){
            return null;
        }
        return command.pack(info);
    }

    public byte[] pack(int commandNo, int boardNo, int data){
        return pack(commandNo, boardNo, new int[]{data});
    }

    public LcResult parse(byte[] packet){
        int commandNo = LcCommand.getCommandNo(packet);
        LcCommand command = LcCommand.getInstance(commandNo);
        return command.parse(packet);
    }
}
