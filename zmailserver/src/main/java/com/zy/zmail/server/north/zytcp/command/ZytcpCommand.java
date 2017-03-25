package com.zy.zmail.server.north.zytcp.command;

import com.zy.zmail.server.north.DoorMessage;
import com.zy.zmail.server.north.DoorResult;
import com.zy.zmail.server.north.util.Octet;

/**
 * Created by wenliz on 2017/1/18.
 */
abstract public class ZytcpCommand {
    /**
     * 登陆
     */
    public static final int LOGIN = 0x11;

    /**
     * 登陆回复
     */
    public static final int LOGIN_RESPONSE = 0x81;

    public static final int HEARTBEAT = 0x10;
    public static final int HEARTBEAT_RESPONSE = 0x80;

    /**
     * 存货
     */
    public static final int DELIVERY = 0x12;
    public static final int DELIVERY_RESPONSE = 0x82;
    /**
     * 取货
     */
    public static final int PICKUP = 0x13;
    public static final int PICKUP_RESPONSE = 0x83;
    /**
     * 查询存货状态
     */
    public static final int QUERY_STATUS = 0x84;
    public static final int QUERY_STATUS_RESPONSE = 0x14;

    public static final int START_FLAG = 0x5A;
    public static final int START_REQUEST = 0xA5;


    public static final int HEAD_LENGTH = 9;

    protected DoorMessage message;

    protected byte[] getStartFlag(){
        byte[] startFlags  = new byte[2];
        startFlags[0]  = (byte) ZytcpCommand.START_FLAG;
        startFlags[1] = (byte) ZytcpCommand.START_REQUEST;
        return startFlags;
    }

    public byte[] pack(DoorMessage info){
        this.message = info;
        byte[] dataUnits = getDataUnits();
        byte[] data = new byte[HEAD_LENGTH + dataUnits.length];

        //起始标志
        byte[] startFlags = getStartFlag();
        System.arraycopy(startFlags, 0, data, 0, 2);
        //指令长度
        data[2] = Octet.getFirstByte(data.length);
        //命令字
        data[3] = Octet.getFirstByte(message.getCommandNo());
        //区号
        data[4] = Octet.getFirstByte(message.getSectionNo());
        //栋号
        data[5] = Octet.getFirstByte(message.getBuildingNo());
        //单元号
        data[6] = Octet.getFirstByte(message.getUnitNo());
        if(message.getFloorNo()!=null && message.getFloorNo() == 0){   //楼层未填
            int floorNo = message.getRoomNo()/100;
            int roomNo = message.getRoomNo() - floorNo * 100;
            data[7] = Octet.getFirstByte(floorNo);
            data[8] = Octet.getFirstByte(roomNo);
        }
        else {
            //楼层
            data[7] = Octet.getFirstByte(message.getFloorNo());
            //房号
            data[8] = Octet.getFirstByte(message.getRoomNo());
        }


        System.arraycopy(dataUnits, 0, data, 9, dataUnits.length);
        adjust(data);
        return data;
    }

    public DoorResult parse(byte[] packet){
        DoorResult result = new DoorResult();
        int length = packet[2];

        result.setCommandNo(packet[3]&0xFF);
        result.setBuildingNo(packet[5]&0xFF);
        result.setUnitNo(packet[6]&0xFF);
        result.setFloorNo(packet[7]&0xFF);
        result.setRoomNo(packet[8]&0xFF);
        byte[] data = new byte[packet.length - HEAD_LENGTH];
        System.arraycopy(packet, HEAD_LENGTH, data, 0, data.length);
        parseUnit(data, result);

        return result;
    }

    abstract protected byte[] getDataUnits();
    abstract protected void adjust(byte[] data);
    abstract protected void parseUnit(byte[] data, DoorResult result);

    public static int getCommandNo(byte[] packet){
        return (int)(packet[3] & 0xFF);
    }

    public static ZytcpCommand getInstance(int commandNo){
        switch (commandNo){
            case ZytcpCommand.LOGIN:
            case ZytcpCommand.LOGIN_RESPONSE:
                return new LoginCommand();
            case ZytcpCommand.HEARTBEAT:
            case ZytcpCommand.HEARTBEAT_RESPONSE:
                return new HeartbeatCommand();
            case ZytcpCommand.DELIVERY:
            case ZytcpCommand.DELIVERY_RESPONSE:
                return new DeliveryCommand();
            case ZytcpCommand.PICKUP:
            case ZytcpCommand.PICKUP_RESPONSE:
                return new PickupCommand();
            case ZytcpCommand.QUERY_STATUS:
            case ZytcpCommand.QUERY_STATUS_RESPONSE:
                return new QueryStatusCommand();

        }

        return null;
    }
}
