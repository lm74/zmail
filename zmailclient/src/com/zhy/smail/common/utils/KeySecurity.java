package com.zhy.smail.common.utils;

import java.security.MessageDigest;

/**
 * Created by wenliz on 2017/1/22.
 */
public class KeySecurity
{
    final static char[] digits = {
            '0' , '1' , '2' , '3' , '4' , '5' ,
            '6' , '7' , '8' , '9' , 'A' , 'B' ,
            'C' , 'D' , 'E' , 'F'
    };

    private static String binToHex(byte aByte)
    {
        char[] tC = new char[2];
        tC[0] = digits[(aByte & 0xF0) >>>  4];
        tC[1] = digits[ aByte & 0x0F ];

        return new String(tC);
    }

    private static byte HexToBin(String aHexStr)
    {
        return (byte)Integer.parseInt(aHexStr, 16);
    }

    /**
     *
     *
     * @param srcStr  ԭ
     * @param key
     * @return
     */
    public static String encryptString(String srcStr, String key)
    {
        byte[] buff = new byte[512];
        //byte[] encStr;
        byte[] bS = srcStr.getBytes();
        byte[] kS = key.getBytes();

        int srcLen = bS.length;
        int keyLen = kS.length;

        if (srcLen > 256){
            srcLen=256;
        }

        int i;

        for(i=0; i < srcLen; i++){
            buff[i + 1] = (byte)(bS[i] + kS[i % keyLen]) ;
        }

        buff[0] = (byte)(srcLen + kS[0]);

        if (srcLen<8) {
            for(i = srcLen + 1; i <= 8; i++)
            {
                buff[i] = (byte)i;
            }
            srcLen = 9;
        } else {
            srcLen = srcLen + 1;
        }

        String tmpStr = "";
        String addStr;

        for(i = 0; i < srcLen; i++){
            addStr = binToHex(buff[i]);
            tmpStr = tmpStr + addStr;
        }

        return tmpStr;
    }

    /**
     *
     *
     * @param srcStr
     * @param key
     * @return
     */
    public static String decryptString(String srcStr, String key)
    {
        int i, len;
        String str1 = "";

        byte[] buff = new byte[512];
        byte[] src = new byte[512];
        byte[] bD = srcStr.getBytes();
        byte[] kS = key.getBytes();

        int srcLen = bD.length;
        int keyLen = kS.length;

        if (srcLen > 256)
        {
            srcLen=256;
        }

        for(i=0; i < srcLen / 2; i++)
        {
            str1 = srcStr.substring(i * 2, i * 2 + 2);
            buff[i] = HexToBin(str1) ;
        }

        len = buff[0] - kS[0];

        for (i = 0; i < len; i++)
        {
            src[i] = (byte)(buff[i + 1] - (byte)kS[i % keyLen]);
        }

        str1 = new String(src);
        str1 = str1.substring(0, len -1);

        return str1;
    }

    //SHA-1
    public static String encrypt(String password){
        if (password == null){
            password = "0000";
        }
        byte[] data = password.getBytes();
        try{
            MessageDigest sha = MessageDigest.getInstance("SHA-1");

            sha.update(data);

            return byteToHex(sha.digest());
        }
        catch(Exception e){
            return password;
        }
    }

    private static String byteToHex(byte[] b){
        String hs="";
        String stmp="";
        for (int n=0;n<b.length; n++)  {
            stmp=Integer.toHexString(b[n] & 0XFF);
            if (stmp.length()==1)
                hs=hs+"0"+stmp;
            else hs=hs+stmp;
        }
        return hs.toUpperCase();
    }

    public static void main(String[] args){
        System.out.println("Password:" + encrypt("888"));
    }
}
