package com.zhy.smail.config;

import com.zhy.smail.serial.SerialPortInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Properties;

/**
 * Created by wenliz on 2017/1/20.
 */
public class LocalConfig {
    public static final int APP_MASTER = 0;
    public static final int APP_SLAVE = 1;
    private static Log log = LogFactory.getLog(LocalConfig.class);
    private static LocalConfig instance;
    //SERIAL
    private final static String SERIAL_PORT = "serialPort";
    private final static String SERIAL_BAUD_RATE = "serialBaudRate";
    private final static String SERIAL_DATA_BITS = "serialDataBits";
    private final static String SERIAL_STOP_BITS = "serialStopBits";
    private final static String SERIAL_PARITY = "serialParity";
    //Other
    private final static String LOCAL_CABINET = "localCabinet";
    private final static String APP_MODE = "appMode";
    private final static String SERVER_IP = "serverIP";
    private final static String SERVER_URL = "serverUrl";
    private final static String LOCAL_URL = "localUrl";
    private final static String REGISTER_NO = "registerNo";
    private final static String VIDEO_FILE = "videoFile";
    // Logo图片
    private final static String LOGO_IMAGE = "logoImage";
    private Properties properties;
    private SerialPortInfo serialPortInfo;
    private String localCabinet;
    private Integer appMode;
    private String serverIP;
    private String registerNo;
    private String videoFile;
    private String logoImage;
    private String serverUrl;
    private String localUrl;

    public LocalConfig() {
        loadProperties();
    }

    synchronized public static LocalConfig getInstance() {
        if (instance == null) {
            instance = new LocalConfig();
        }
        return instance;
    }

    private String getPropertiesFile() {
        // 得到当前应用程序（无论是class还是Jar包）的绝对根路径
        URL url = getClass().getProtectionDomain().getCodeSource().getLocation();
        String file = null;
        try {
            file = URLDecoder.decode(url.getPath(), "UTF-8"); // 路径如果包含中文进行编码转换
        } catch (UnsupportedEncodingException e) {
            log.error(e.getCause());
        }
        log.info(file);

        // 掐去第一个字符"/"
        if (file.startsWith("/")) {
            file = file.substring(1);
        }
        log.info(file);

        // 去掉尾部的"xxx.jar"字样
        if (file.endsWith(".jar")) {
            file = file.substring(0, file.lastIndexOf("/") + 1);
        }
        log.info(file);

        // 得到真正结果
        file = file + "config.properties";
        log.info(file);
        return file;
    }


    public void loadProperties() {
        log.info("try to open config.properties.");
        String path = getPropertiesFile();
        File file = new File(path);
        try {
            FileInputStream is = new FileInputStream(file);
            properties = new Properties();
            properties.load(is);
            log.info("Try to parse config.properites.");
            loadSerialPort();
            setLocalCabinet(getString(LOCAL_CABINET, "0"));
            setAppMode(getInteger(APP_MODE, 0));
            setServerIP(getString(SERVER_IP, "127.0.0.1"));
            setServerUrl(getString(SERVER_URL, ""));
            setLocalUrl(getString(LOCAL_URL, "http://127.0.0.1:8080/api"));
            setRegisterNo(getString(REGISTER_NO, "0000"));
            setVideoFile(getString(VIDEO_FILE, ""));
            setLogoImage(getString(LOGO_IMAGE, ""));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            log.error("config配置文件不存在，config配置文件的路径为：" + file.getPath());
        } catch (IOException e) {
            e.printStackTrace();
            log.error("装入config配置文件时出错，错误信息为：" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //设置
    private void setSerialSaveProperties() {
        //SERIAL
        String strSerialPort = "";
        String strSerialBaudRate = "";
        String strSerialDataBits = "";
        String strSerialStopBits = "";
        String strSerialParity = "";
        strSerialPort = serialPortInfo.getPortName();
        strSerialBaudRate = serialPortInfo.getBaudRate().toString();
        strSerialDataBits = serialPortInfo.getDataBits().toString();
        strSerialStopBits = serialPortInfo.getStopBits().toString();
        strSerialParity = serialPortInfo.getParity().toString();
        properties.setProperty(SERIAL_PORT, strSerialPort);
        properties.setProperty(SERIAL_BAUD_RATE, strSerialBaudRate);
        properties.setProperty(SERIAL_DATA_BITS, strSerialDataBits);
        properties.setProperty(SERIAL_STOP_BITS, strSerialStopBits);
        properties.setProperty(SERIAL_PARITY, strSerialParity);
        properties.setProperty("pollingEnable", serialPortInfo.getPollingEnable().toString());
        properties.setProperty("pollingPeriod", serialPortInfo.getPollingPeriod().toString());
    }

    public void saveProperties() {
        String path = getPropertiesFile();
        File file = new File(path);
        try {
            this.setSerialSaveProperties();
            properties.setProperty(LOCAL_CABINET, getLocalCabinet());
            properties.setProperty(APP_MODE, getAppMode().toString());
            properties.setProperty(SERVER_IP, getServerIP());
            properties.setProperty(SERVER_URL, getServerUrl());
            properties.setProperty(LOCAL_URL, getLocalUrl());
            properties.setProperty(REGISTER_NO, getRegisterNo());
            properties.setProperty(VIDEO_FILE, getVideoFile());
            properties.setProperty(LOGO_IMAGE, getLogoImage());
            FileOutputStream output = new FileOutputStream(file);
            properties.store(output, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void exportData(File file) {
        Properties prop = new Properties();
        try {
            FileOutputStream out = new FileOutputStream(file);
            prop.store(out, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void importData(File file) {
        Properties prop = new Properties();
        try {
            FileInputStream is = new FileInputStream(file);
            prop.load(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void restoreData() {
        loadProperties();
    }

    private void loadSerialPort() {
        try {
            String portNames = getString(SERIAL_PORT, "COM1");
            String baudRates = getString(SERIAL_BAUD_RATE, "115200");
            String dataBits = getString(SERIAL_DATA_BITS, "8");
            String stopBits = getString(SERIAL_STOP_BITS, "1");
            String paritys = getString(SERIAL_PARITY, "0");
            Integer pollingEnable = getInteger("pollingEnable", 1);
            Integer pollingPeriod = getInteger("pollingPeriod", 5);
            serialPortInfo = new SerialPortInfo();
            serialPortInfo.setPortName(portNames);
            serialPortInfo.setBaudRate(Integer.parseInt(baudRates));
            serialPortInfo.setDataBits(Integer.parseInt(dataBits));
            serialPortInfo.setStopBits(Integer.parseInt(stopBits));
            serialPortInfo.setParity(Integer.parseInt(paritys));
            serialPortInfo.setPollingEnable(pollingEnable);
            serialPortInfo.setPollingPeriod(pollingPeriod);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Boolean getBoolean(String s, Boolean defaultValue) {
        Boolean result = defaultValue;
        try {
            String b = properties.getProperty(s);
            if (b.equals("1")) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error("Can't get the Boolean value of " + s + ": " + e.getMessage());
        }
        return result;
    }

    public String getString(String s, String defaultValue) {
        return getString(properties, s, defaultValue);
    }

    public String getString(Properties prop, String s, String defaultValue) {
        String s1 = defaultValue;
        try {
            s1 = properties.getProperty(s);
        } catch (Exception e) {
            log.error("Can't get the value of " + s);
        }
        return s1;
    }

    public Integer getInteger(String s, Integer defaultValue) {
        return getInteger(properties, s, defaultValue);
    }

    public Integer getInteger(Properties prop, String s, Integer defaultValue) {
        Integer result = defaultValue;
        try {
            result = Integer.parseInt(prop.getProperty(s));
        } catch (Exception e) {
            log.error("Can't get  the vlaue of " + s);
        }
        return result;
    }

    public Double getDouble(String s, Double defaultValue) {
        return getDouble(properties, s, defaultValue);
    }

    public Double getDouble(Properties prop, String s, Double defaultValue) {
        Double result = defaultValue;
        try {
            result = Double.parseDouble(prop.getProperty(s));
        } catch (Exception e) {
            log.error("Can't get  the vlaue of " + s);
        }
        return result;
    }

    protected Properties getProperties() {
        if (properties == null)
            loadProperties();
        return properties;
    }


    public String getLogoImage() {
        return logoImage;
    }

    public void setLogoImage(String logoImage) {
        this.logoImage = logoImage;
    }

    public String getVideoFile() {
        return videoFile;
    }

    public void setVideoFile(String videoFile) {
        this.videoFile = videoFile;
    }

    public String getRegisterNo() {
        return registerNo;
    }

    public void setRegisterNo(String registerNo) {
        this.registerNo = registerNo;
    }

    public String getServerIP() {
        return serverIP;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    public String getLocalCabinet() {
        return localCabinet;
    }

    public void setLocalCabinet(String localCabinet) {
        this.localCabinet = localCabinet;
    }

    public Integer getAppMode() {
        return appMode;
    }

    public void setAppMode(Integer appMode) {
        this.appMode = appMode;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getLocalUrl() {
        return localUrl;
    }

    public void setLocalUrl(String localUrl) {
        this.localUrl = localUrl;
    }

    public SerialPortInfo getSerialPortInfo() {
        return serialPortInfo;
    }

    public void setSerialPortInfo(SerialPortInfo serialPortInfo) {
        this.serialPortInfo = serialPortInfo;
    }

}
