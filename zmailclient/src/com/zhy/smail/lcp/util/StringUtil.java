package com.zhy.smail.lcp.util;

import java.util.*;

/**
 * Created by wenliz on 2017/1/20.
 */
public class StringUtil {
    public static String trim(String s) {
        s = s.trim();
        int pos = s.indexOf(0);
        if (pos > 0) {
            s = s.substring(0, pos);
        }
        return s;
    }

    public static byte[] toBytes(String source, int len) {
        byte[] original, result = new byte[len];
        original = toBytes(source);
        if (original.length > len) {
            copyTo(original, result);
        }
        else {
            Arrays.fill(result, (byte) 0);
            copyTo(original, result);
        }
        return result;
    }

    public static void copyTo(byte[] source, byte[] dest) {
        int length = (source.length > dest.length ? dest.length : source.length);
        for (int i = 0; i < length; i++) {
            dest[i] = source[i];
        }
    }

    /**
     * 转换为字符串二进制流
     * @param source String
     * @return byte[]
     */
    public static byte[] toBytes(String source) {
        byte[] result;
        try {
            result = source.getBytes();
        }
        catch (Exception e) {
            result = source.getBytes();
        }
        return result;
    }

    /**
     * 二进制流转换为字符串
     * @param packet byte[]
     * @return String
     */
    public static String parse(byte[] packet) {
        if (packet == null) {
            return "";
        }

        if (packet.length <= 0) {
            return "";
        }
        return HexString.toString(packet);

        /*if (isVisualFlag(packet[0])) {
            return new String(packet);
        } else if (packet[0] == (byte)0x7E) {
            return HexString.toString(packet);
        } else {
            return new String(packet);
            //return HexString.toStringX(packet);
        }*/
    }

    /**
     * 是否可见包标识
     * @param b
     * @return
     */
    public static boolean isVisualFlag(byte b) {
        if ((b == (byte)0x21)	// '!', 中移协议
                || (b == (byte)0x58)	// 'X', 联通协议
                || (b == (byte)0x30)	// '0', 京信协议
                || (b == (byte)0x31) 	// '1', 京信协议
                || (b == (byte)0x41)    // 'A', BTI老协议
                || (b == (byte)0x50)    // 'P', BTI老协议
                || (b == (byte)0x37)	) {	// '7'
            return true;
        } else {
            return false;
        }

    }

    public static String toHexString(byte[] source){
        StringBuffer buffer = new StringBuffer("");
        for (int i = 0; i < source.length; i++){
            buffer.append(Integer.toHexString(source[i]));
        }
        return buffer.toString();
    }

    /**
     * 提取协议数据包
     * @param statrFlag
     * @param endFlag
     * @param data
     * @return
     */
    public static byte[] extractPacket(byte statrFlag, byte endFlag, byte[] data) {
        List<Byte> packetBytes = new ArrayList<Byte>();
        boolean foundStartFlag = false;
        boolean foundEndFlag = false;
        for (byte b: data) {
            if (!foundStartFlag) {
                if (b == statrFlag) {
                    foundStartFlag = true;
                    packetBytes.add(b);
                }
            } else {
                packetBytes.add(b);
                if (b == endFlag) {
                    foundEndFlag = true;
                    break;
                }
            }
        }

        if (!foundEndFlag) {
            return null;
        }

        byte[] packet = new byte[packetBytes.size()];
        for(int i = 0; i < packet.length; i++) {
            packet[i] = packetBytes.get(i);
        }

        return packet;
    }


    public static String replace(String source, String oldStr , String newStr){
        String result = source;
        int index = source.indexOf(oldStr);
        if (index >= 0){
            String begin = source.substring(0, index);
            String end = source.substring(index + oldStr.length());
            result = begin + newStr + end;
        }
        return result;
    }

    public static Boolean isEmpty(String source){
        if((source == null) || (source.length() == 0)){
            return true;
        }
        return false;
    }

    public static Map<String, String> toMap(String source, String regex){
        String[] sourceArray = source.split(regex);
        Map<String, String> results = new HashMap<String, String>();
        for(int i=0; i<sourceArray.length; i++){
            String one = sourceArray[i].trim();
            int index = one.indexOf("=");
            if(index<0) continue;

            String key = one.substring(0, index);
            String value = one.substring(index+1);
            results.put(key, value);
        }
        return results;
    }
}
