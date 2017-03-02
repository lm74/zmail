package com.zhy.smail.lcp.command;

import com.zhy.smail.lcp.LcResult;
import com.zhy.smail.lcp.util.CRC8;
import com.zhy.smail.lcp.util.Octet;

import java.lang.reflect.Array;

import static com.zhy.smail.lcp.util.CRC8.calcCrc8;

/**
 * Created by wenliz on 2017/1/18.
 */
abstract public class LcCommand {
    /**
     * 读取版本号
     */
    public static final int READ_VERSION = 0x01;

    /**
     * 读取机号
     */
    public static final int READ_BOARDNO = 0x04;

    public static final int BEEP = 0x10;

    /**
     * 开箱
     */
    public static final int OPEN_BOX = 0xA1;
    /**
     * 读取箱门状态
     */
    public static final int READ_BOX_STATUS = 0xA3;

    public static final int START_FLAG = 0xAA;
    public static final int START_REQUEST = 0xAC;
    public static final int START_RESPONSE = 0xCA;

    public static final byte VERSION = 0x20;
    public static final byte TYPE = 0x00;
    public static final byte PLAN_TEXT = 0x00;

    public static final int END_FLAG = 0XBB;

    public static final int HEAD_LENGTH = 11;

    protected  CommandInfo commandInfo;

    protected byte[] getStartFlag(){
        byte[] startFlags  = new byte[2];
        startFlags[0]  = (byte)LcCommand.START_FLAG;
        startFlags[1] = (byte)LcCommand.START_REQUEST;
        return startFlags;
    }

    public byte[] pack(CommandInfo info){
        this.commandInfo = info;
        byte[] dataUnits = getDataUnits();
        byte[] data = new byte[HEAD_LENGTH + dataUnits.length];

        //起始标志
        byte[] startFlags = getStartFlag();
        System.arraycopy(startFlags, 0, data, 0, 2);
        //版本号
        data[2] = VERSION;
        data[3] = TYPE;
        byte[] address = Octet.toData(info.getBoardNo());
        System.arraycopy(address, 0, data, 4, 4);
        data[8] = PLAN_TEXT;
        System.arraycopy(dataUnits, 0, data, 9, dataUnits.length);
        data[data.length-1] =(byte) END_FLAG;

        // 校验计算
        byte[] dataToCheck = new byte[data.length - 4];
        System.arraycopy(data, 2, dataToCheck,  0, dataToCheck.length);
        byte code = CRC8.calcXor(dataToCheck);
        data[data.length-2]  = code;

        return data;
    }

    public LcResult parse(byte[] packet){
        LcResult result = new LcResult();
        byte[] address = new byte[4];
        System.arraycopy(packet, 4, address, 0, 4);
        result.setBoardNo(Octet.toInt(address));

        byte[] data = new byte[packet.length - HEAD_LENGTH];
        System.arraycopy(packet, HEAD_LENGTH-2, data, 0, data.length);
        parseUnit(data, result);

        return result;
    }

    abstract protected byte[] getDataUnits();
    abstract protected void parseUnit(byte[] data, LcResult result);

    public static int getCommandNo(byte[] packet){
        return (int)(packet[9] & 0xFF);
    }

    public static LcCommand getInstance(int commandNo){
        switch (commandNo){
            case LcCommand.BEEP:
                return new BeepCommand();

            case LcCommand.READ_BOARDNO:
                return new ReadBoardNo();
            case LcCommand.READ_VERSION:
                return new ReadVersion();
            case LcCommand.READ_BOX_STATUS:
                return new ReadBoxStatus();
            case LcCommand.OPEN_BOX:
                return new OpenBoxCommand();

        }

        return null;
    }
}
