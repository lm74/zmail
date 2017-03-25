package com.zhy.smail.common.utils;

import com.zhy.smail.MainApp;
import com.zhy.smail.component.FileUtils;
import com.zhy.smail.component.SimpleDialog;
import com.zhy.smail.config.GlobalOption;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * Created by wenliz on 2017/3/6.
 */
public class SystemUtil {
    public static String getMacAddress() {
        try {
            MainApp app = new MainApp();
            StringBuffer sb = new StringBuffer("");
            InetAddress ia = Inet4Address.getLocalHost();
            byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
            // Modified By 罗鹏 Mar 21 2017
            if (mac == null) {
                //SimpleDialog.showAutoCloseInfo(app.getRootStage(), "网络有问题，请检查网络设置！");
                return "";
            } else {
                for (int i = 0; i < mac.length; i++) {
                    int temp = mac[i] & 0xFF;
                    String str = Integer.toHexString(temp);
                    sb.append(str);
                }
            }
            // Ended By 罗鹏 Mar 21 2017
            return sb.toString();

        } catch (UnknownHostException e) {


        } catch (SocketException e) {

        }
        return "000000000000";
    }

    public static String getRegisterNo() {
        String serialNo = getSerialNo();
        String encry2 = KeySecurity.encrypt(serialNo);
        String registerNo = "";
        for (int i = 0; i < 4; i++) {
            registerNo += "-" + encry2.substring(i * 4, i * 4 + 4);
        }

        return registerNo.substring(1);
    }

    public static String getSerialNo() {
        String mac = SystemUtil.getMacAddress();

        if(mac.length() == 0){
            mac = FileUtils.readMac();
            if(mac == null || mac.length() ==0){
                return "";
            }
        }
        else{
            FileUtils.writeMac(mac);
        }
        //String mac = SystemUtil.getDiskSerialNo();

        String encry = KeySecurity.encrypt(mac);
        String serialNo = encry.substring(0, 12);
        return serialNo;
    }

    public static String getDiskSerialNo() {
        String line = "";
        String HdSerial = "";//定义变量
        try{
            Process proces = Runtime.getRuntime().exec("cmd /c dir c:");//获取命令行参数

            BufferedReader buffreader = new BufferedReader(
                    new InputStreamReader(proces.getInputStream(),"GB2312"));

            while ((line = buffreader.readLine()) != null) {

                System.out.println(line);
                if (line.indexOf("卷的序列号是") != -1) {  //读取参数并获取硬盘序列号
                    HdSerial = line.substring(line.indexOf("卷的序列号是") + "卷的序列号是".length(),
                            line.length());
                    System.out.println(HdSerial);
                    break;

                }
            }

        } catch (IOException e) {
            e.printStackTrace();

        }

        return HdSerial;//返回硬盘序列号卷的序列 非物理
    }


    public static boolean canUse() {
        if (GlobalOption.useDays != null && GlobalOption.useDays.getIntValue() > 0) {
            if (GlobalOption.useStart != null && GlobalOption.useStart.getDateValue() != null) {
                Timestamp useStart = GlobalOption.useStart.getDateValue();
                Integer useDays = GlobalOption.useDays.getIntValue();
                Calendar start = Calendar.getInstance();
                start.setTime(useStart);
                Calendar now = Calendar.getInstance();
                start.add(Calendar.DAY_OF_MONTH, useDays);
                if (start.getTimeInMillis() < now.getTimeInMillis()) {
                    return false;
                }
            }
        }
        return true;
    }

    public static String getMotherboardSN() {//主板ID
        String result = "";
        try {
            File file = File.createTempFile("realhowto",".vbs");
            file.deleteOnExit();
            FileWriter fw = new java.io.FileWriter(file);

            String vbs =
                    "Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\n"
                            + "Set colItems = objWMIService.ExecQuery _ \n"
                            + "   (\"Select * from Win32_BaseBoard\") \n"
                            + "For Each objItem in colItems \n"
                            + "    Wscript.Echo objItem.SerialNumber \n"
                            + "    exit for  ' do the first cpu only! \n"
                            + "Next \n";

            fw.write(vbs);
            fw.close();
            Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
            BufferedReader input =
                    new BufferedReader
                            (new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                result += line;
            }
            input.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return result.trim();
    }


    public static String getCPUSerial() {//CPUId
        String result = "";
        try {
            File file = File.createTempFile("tmp", ".vbs");
            file.deleteOnExit();
            FileWriter fw = new java.io.FileWriter(file);

            String vbs = "On Error Resume Next \r\n\r\n" + "strComputer = \".\"  \r\n"
                    + "Set objWMIService = GetObject(\"winmgmts:\" _ \r\n"
                    + "    & \"{impersonationLevel=impersonate}!\\\\\" & strComputer & \"\\root\\cimv2\") \r\n"
                    + "Set colItems = objWMIService.ExecQuery(\"Select * from Win32_Processor\")  \r\n "
                    + "For Each objItem in colItems\r\n " + "    Wscript.Echo objItem.ProcessorId  \r\n "
                    + "    exit for  ' do the first cpu only! \r\n" + "Next                    ";

            fw.write(vbs);
            fw.close();
            Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                result += line;
            }
            input.close();
            file.delete();
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        if (result.trim().length() < 1 || result == null) {
            result = "无CPU_ID被读取";
        }
        return result.trim();
    }
}
