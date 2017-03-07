package com.zhy.smail.common.utils;

import java.net.*;

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
}
