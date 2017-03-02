package com.zhy.smail.lcp.util;

/**
 * Created by wenliz on 2017/1/20.
 */

import java.util.ArrayList;
import java.util.List;

/**
 * 十进制字符串类
 *
 */
public class HexString {
    /**
     * 将十六进制字符串转换成对应的字节数组
     * 注意: 每两个十六进制字符合并为一个字节,非十六进制字符将被忽略
     * @param hexString 要转换的十六进制字符串
     * @return 字节数组
     */
    public static byte[] getBytes(String hexString) {
        List<Byte> dataList = new ArrayList<Byte>();
        int dataLen = 0;
        boolean gotHarfByte = false;

        for (int i = 0; i < hexString.length(); i++) {
            char currentChar = hexString.charAt(i);
            if (!isHexDigit(currentChar))
            {
                continue;
            }

            byte currentValue = (byte)Integer.parseInt(Character.toString(currentChar), 16);
            if (gotHarfByte) {
                byte value = (byte)(dataList.get(dataLen) +  currentValue);
                dataList.set(dataLen, value);
                dataLen++;
                gotHarfByte = false;
            } else {
                byte value = (byte)(currentValue << 4);
                dataList.add(value);
                gotHarfByte = true;
            }
        }

        byte[] data = new byte[dataLen];
        for (int i = 0; i< dataLen; i++) {
            data[i] = dataList.get(i);
        }
        return data;
    }

    private static boolean isHexDigit(char ch) {
        boolean result = false;
        if (Character.isDigit(ch)) {
            result =  true;
        } else if ((ch >= 'A') && (ch <='F')) {
            result = true;
        } else if ((ch >= 'a') && (ch <='f')) {
            result = true;
        } else {
            result = false;
        }

        return result;
    }

    /**
     * 将字节数组转化为十六进制字符串
     * @param bytes 字节数组
     * @return 十六进制字符串
     */
    public static String toString(byte[] bytes) {
        String hexString = "";
        for(byte b: bytes) {
            hexString += String.format("%02X", b);
        }

        return hexString;
    }
    public static String toString(byte[] data, int offset, int count) {
        String hexString = "";
        for(int i = offset;i < count; i++) {
            hexString += String.format("%02X", data[i]);
        }

        return hexString;
    }

    /**
     * 将字节数组转化为十六进制字符串
     * @param bytes 字节数组
     * @return 十六进制字符串
     */
    public static String toStringX(byte[] bytes) {
        String hexString = "";
        for(byte b: bytes) {
            hexString += String.format("%02X ", b);
        }

        return hexString;
    }
}