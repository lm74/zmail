package com.zhy.smail.task;

import com.zhy.smail.lcp.LcProtocol;
import com.zhy.smail.lcp.LcResult;
import com.zhy.smail.lcp.ProtocolException;
import com.zhy.smail.lcp.util.StringUtil;
import com.zhy.smail.serial.SerialGateway;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by wenliz on 2017/1/20.
 */
public class ResponseManager  implements Runnable{
    public final static int WAIT_SECONDS = 5;
    public static BlockingQueue<LcResult> response = new LinkedBlockingQueue<LcResult>();
    private static Log log = LogFactory.getLog(ResponseManager.class);

    private boolean canceled = false;

    public void setCanceled(boolean b){
        this.canceled = b;
    }

    public void run(){
        while(!canceled) {
            try {
                byte[] packet = SerialGateway.received.poll(500L, TimeUnit.MILLISECONDS);
                if(packet != null) {

                    LcProtocol protocol = new LcProtocol();
                    LcResult result = protocol.parse(packet);
                    response.put(result);

                    log.info("数据包:" + StringUtil.parse(packet)+" 处理成功");
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            /*catch(ProtocolException e){
                log.info("解释数据包出错:" + e.getMessage());
            }*/
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
