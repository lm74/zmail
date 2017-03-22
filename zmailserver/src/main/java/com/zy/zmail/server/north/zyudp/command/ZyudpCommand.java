package com.zy.zmail.server.north.zyudp.command;

import com.zy.zmail.server.north.DoorMessage;
import com.zy.zmail.server.north.DoorResult;
import com.zy.zmail.server.north.util.CRC8;
import com.zy.zmail.server.north.util.HexString;
import com.zy.zmail.server.north.util.Octet;


import java.util.Calendar;
import java.util.Date;


/**
 * Created by wenliz on 2017/1/18.
 */
 public abstract  class ZyudpCommand {


    /**
     * 存货
     */
    public static final int DELIVERY = 0x11;
    public static final int DELIVERY_RESPONSE = 0xA1;
    /**
     * 取货
     */
    public static final int PICKUP = 0x12;
    public static final int PICKUP_RESPONSE = 0xA2;
    /**
     * 查询存货状态
     */
    public static final int QUERY_STATUS = 0xB1;
    public static final int QUERY_STATUS_RESPONSE = 0x21;

    public static final int START_FLAG = 0x5A;
    public static final int START_REQUEST = 0xA5;

    public static final int END_FLAG = 0xED;


    public static final int HEAD_LENGTH = 11;

    protected DoorMessage message;

    protected byte[] getStartFlag(){
        byte[] startFlags  = new byte[2];
        startFlags[0]  = (byte) ZyudpCommand.START_FLAG;
        startFlags[1] = (byte) ZyudpCommand.START_REQUEST;
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
        //楼层
        data[7] = Octet.getFirstByte(message.getFloorNo());
        //房号
        data[8] = Octet.getFirstByte(message.getRoomNo());
        System.arraycopy(dataUnits, 0, data, 9, dataUnits.length);
        adjust(data);


        // 校验计算
        byte[] dataToCheck = new byte[ data.length- 2];
        System.arraycopy(data, 0, dataToCheck,  0, dataToCheck.length);
        byte code = CRC8.calcXor(dataToCheck);
        data[data.length-2]  = code;
        data[data.length-1] = (byte)(END_FLAG & 0xFF);
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
        System.arraycopy(packet, HEAD_LENGTH-2, data, 0, data.length);
        parseUnit(data, result);

        return result;
    }

    protected void getDeliveryTime(byte[] data, int index){
        if(message.getDeliveryTime() == null){
            message.setDeliveryTime(new Date(System.currentTimeMillis()));
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(message.getDeliveryTime());
        int year = calendar.get(Calendar.YEAR);
        year = year % 100;
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        data[index++] = (byte)year;
        data[index++] = (byte)month;
        data[index++] = (byte)day;
        data[index++] = (byte)hour;
        data[index++] = (byte)minute;
        data[index++] = (byte)second;

    }

    abstract protected byte[] getDataUnits();
    abstract protected void adjust(byte[] data);
    abstract protected void parseUnit(byte[] data, DoorResult result);

    public static int getCommandNo(byte[] packet){
        return (int)(packet[3] & 0xFF);
    }

    public static ZyudpCommand getInstance(int commandNo){
        switch (commandNo){
            case ZyudpCommand.DELIVERY:
            case ZyudpCommand.DELIVERY_RESPONSE:
                return new DeliveryCommand();
            case ZyudpCommand.PICKUP:
            case ZyudpCommand.PICKUP_RESPONSE:
                return new PickupCommand();
            case ZyudpCommand.QUERY_STATUS:
            case ZyudpCommand.QUERY_STATUS_RESPONSE:
                return new QueryStatusCommand();

        }

        return null;
    }
}
