package com.zhy.smail.serial;

import com.zhy.smail.config.LocalConfig;
import com.zhy.smail.lcp.command.LcCommand;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.InputStream;

/**
 * 串口读类，当串口有数据时，读出数据并进行分包
 * @author wenliz
 *
 */
public class SerialReader implements SerialPortEventListener {
    private static Log log = LogFactory.getLog(LocalConfig.class);


    private InputStream in;
    private byte[] oldData;
    private SerialGateway gateway;
    private byte preFlag;  //由于中移协议中有可能出现0x58，因此在串口打开后，就确定协议的类型

    public SerialReader(SerialGateway gateway, InputStream in){
        this.in = in;
        this.gateway = gateway;
        preFlag = 0;
    }
    public void serialEvent(SerialPortEvent event){

        byte[] buffer = new byte[2048];
        int length;
        try{
            length = in.read(buffer);
            if (length > 0){
                byte[] data = new byte[length];
                System.arraycopy(buffer, 0, data, 0, length);
                // log.debug("从串口收到数据:" + StringUtils.parse(data));
                put(data);
            }

        }
        catch(Exception e){
            log.error("receving data is failed, because:" + e.getMessage());
        }
    }

    private void put(byte[] data) throws InterruptedException{
        byte[] newData;
        if (oldData == null){
            newData = data;
        }
        else{
            newData = new byte[oldData.length + data.length];
            System.arraycopy(oldData, 0, newData, 0, oldData.length);
            System.arraycopy(data, 0, newData, oldData.length,  data.length);
            oldData = null;
        }

        while (newData != null){
            int startIndex = getStartIndex(newData, 0);
            if (startIndex == -1) return;

            int endIndex = getEndIndex(newData, startIndex + 1);
            if (endIndex == -1){
                int oldLength = newData.length - startIndex ;
                oldData = new byte[oldLength];
                System.arraycopy(newData, startIndex, oldData, 0, oldLength);
                //log.debug("留下的数据:" + StringUtils.parse(oldData));
                return;
            }
            else{
                int oneLength = endIndex - startIndex + 1;
                if (oneLength == 2){
                    byte[] elseData = new byte[newData.length - 1];
                    System.arraycopy(newData, endIndex, elseData, 0, newData.length - 1);
                    newData = elseData;
                    continue;
                }
                byte[] one = new byte[endIndex - startIndex +1 ];
                System.arraycopy(newData, startIndex, one, 0, endIndex - startIndex + 1);
                gateway.put(one);
                int elseLength = newData.length - endIndex - 1 ;
                if (elseLength>0){
                    byte[] elseData = new byte[elseLength];
                    System.arraycopy(newData, endIndex + 1, elseData, 0, elseLength);
                    newData = elseData;
                }
                else{
                    preFlag = 0; //协议标志清0，可以接收任务类型的协议
                    return;
                }
            }
        }
    }

    private int getStartIndex(byte[] data, int startIndex){
        for(int i=startIndex; i<data.length-1; i++){
            if (data[i] == (byte)LcCommand.START_FLAG &&  data[i+1] == (byte)LcCommand.START_RESPONSE){
                return i;
            }
        }

        return -1;
    }

    private int getEndIndex(byte[] data, int startIndex){
        for(int i=startIndex; i<data.length; i++){
            if (data[i] == (byte)LcCommand.END_FLAG ){
                return i;
            }
        }

        return -1;
    }
}