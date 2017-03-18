package com.zy.zmail.server.north.zyudp.command;

import com.zy.zmail.server.north.DoorMessage;
import com.zy.zmail.server.north.util.CRC8;
import com.zy.zmail.server.north.util.HexString;
import com.zy.zmail.server.north.util.Octet;

import java.util.Calendar;
import java.util.Date;


/**
 * Created by wenliz on 2017/1/18.
 */
 public class ZyudpCommand {
    public static final int START_FLAG = 0xAA;
    public static final int START_REQUEST = 0xBB;


    public static final byte VERSION = 0x20;
    public static final byte TYPE = 0x00;
    public static final byte PLAN_TEXT = 0x00;

    public static final int END_FLAG = 0XCC;
    public static final int END_FLAG2 = 0XDD;

    public static final int COMMAND_LENGTH = 33;
    private DoorMessage message;
    private int index;

   public ZyudpCommand(DoorMessage message){
       this.message = message;
   }

    protected void setStartAndEndFlag(byte[] data){
        data[index++]  = (byte) ZyudpCommand.START_FLAG;
        data[index++] = (byte) ZyudpCommand.START_REQUEST;

        data[data.length-2] = (byte)ZyudpCommand.END_FLAG;
        data[data.length-1] = (byte)ZyudpCommand.END_FLAG2;
    }

    private void getAddress(byte[] data){
        for(int i=0; i<20; i++) {
            data[index + i] = 0;
        }
        byte[] building = HexString.strToAscii(message.getBuildingNo().toString(),4);
        System.arraycopy(building, 0, data, index, building.length);
        index += building.length;

        byte[] units = HexString.strToAscii(message.getBuildingNo().toString(),2);
        System.arraycopy(units, 0, data, index, units.length);
        index +=units.length;

        byte[] rooms = HexString.strToAscii(message.getRoomNo().toString(),8);
        System.arraycopy(rooms, 0, data,index, rooms.length);
        index += rooms.length;

        byte[] cabinets = HexString.strToAscii(message.getCabinetNo().toString(),4);
        System.arraycopy(cabinets, 0, data,index, cabinets.length);
        index += cabinets.length;

        byte[] boxs = HexString.strToAscii(message.getBoxNo().toString(),2);
        System.arraycopy(boxs, 0, data,index, boxs.length);
        index += boxs.length;
    }

    private void getDeliveryTime(byte[] data){
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

    public byte[] pack(){
        index = 0;
        byte[] data = new byte[COMMAND_LENGTH];

        //标志
        setStartAndEndFlag(data);

        //长度
        data[index++] = (byte)(COMMAND_LENGTH - 6);

        //地址
        getAddress(data);

        data[index++] =  message.getOperateType();

        getDeliveryTime(data);

        // 校验计算
        byte[] dataToCheck = new byte[COMMAND_LENGTH - 6];
        System.arraycopy(data, 3, dataToCheck,  0, dataToCheck.length);
        byte code = CRC8.calcXor(dataToCheck);
        data[data.length-3]  = code;

        return data;
    }





    public static ZyudpCommand getInstance(DoorMessage message){
        return new ZyudpCommand(message);
    }
}
