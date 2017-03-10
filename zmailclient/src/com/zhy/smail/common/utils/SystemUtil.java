package com.zhy.smail.common.utils;

import com.zhy.smail.config.GlobalOption;

import java.net.*;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * Created by wenliz on 2017/3/6.
 */
public class SystemUtil {
    public static String getMacAddress(){
        try {
            InetAddress ia = Inet4Address.getLocalHost();
            byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
            StringBuffer sb = new StringBuffer("");
            for(int i=0; i<mac.length; i++){
                int temp = mac[i] & 0xFF;
                String str = Integer.toHexString(temp);
                sb.append(str);
            }
           return sb.toString();
        }
        catch (UnknownHostException e){

        }
        catch (SocketException e){

        }
        return "000000000000";
    }

    public static String getRegisterNo(){
        String serialNo = getSerialNo();
        String encry2 = KeySecurity.encrypt(serialNo);
        String registerNo = "";
        for(int i=0; i<4; i++){
            registerNo += "-"+ encry2.substring(i*4, i*4+4);
        }

        return registerNo.substring(1);
    }

    public static String getSerialNo(){
        String mac = SystemUtil.getMacAddress();
        String encry = KeySecurity.encrypt(mac);
        String serialNo = encry.substring(0,12);
        return serialNo;
    }

    public static boolean canUse(){
        if(GlobalOption.useDays!=null && GlobalOption.useDays.getIntValue()>0){
            if(GlobalOption.useStart != null && GlobalOption.useStart.getDateValue() != null){
                Timestamp useStart = GlobalOption.useStart.getDateValue();
                Integer useDays = GlobalOption.useDays.getIntValue();
                Calendar start = Calendar.getInstance();
                start.setTime(useStart);
                Calendar now = Calendar.getInstance();
                start.add(Calendar.DAY_OF_MONTH, useDays);
                if(start.getTimeInMillis()<now.getTimeInMillis()){
                    return false;
                }
            }
        }
        return true;
    }
}
