package com.zhy.smail.config;

import com.zhy.smail.cabinet.entity.CabinetInfo;
import com.zhy.smail.setting.entity.SystemOption;
import com.zhy.smail.user.entity.UserInfo;

import java.util.LinkedList;

/**
 * Created by wenliz on 2017/1/22.
 */
public class GlobalOption {
    public static int appMode = 0;
    public static int runMode = 0;
    public static final String localUrl = "http://127.0.0.1:8080/api";
    public static String serverIP = null;


    public static UserInfo currentUser=null;
    public static CabinetInfo currentCabinet = null;
    public static int TimeoutTotal = 99;
    public static LinkedList<String> parents = new LinkedList<>();
    public static SystemOption timeout;
    public static SystemOption buildingNo;
    public static SystemOption unitNo;
    public static SystemOption useDays;
    public static SystemOption useStart;
    public static SystemOption mainTitle;
    public static SystemOption doorProtocol;
    public static SystemOption doorServerIp;
    public static SystemOption doorServerPort;
    public static SystemOption cardNeedPassword;
    public static SystemOption deliverySameMail;
    public static SystemOption remainTime;



    public static String getServerRoot(){
        return "http://" + serverIP+":8080";
    }
    public static String  getServerUrl(){
        return getServerRoot() + "/api";
    }
}
