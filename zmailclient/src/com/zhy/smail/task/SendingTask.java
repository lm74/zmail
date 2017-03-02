package com.zhy.smail.task;


import com.zhy.smail.lcp.LcProtocol;
import com.zhy.smail.lcp.LcResult;
import com.zhy.smail.lcp.ProtocolException;
import javafx.concurrent.Task;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by wenliz on 2017/2/13.
 */
public class SendingTask<Void> extends Task {
    private static final int WAIT_TIME = 10;
    private int[] boxList;
    private boolean isWait;
    private int commandNo;
    private int boardNo;

    public SendingTask(int[] boxList,int commandNo,int boardNo,  boolean isWait){
        this.boxList = boxList;
        this.commandNo = commandNo;
        this.boardNo = boardNo;

        this.isWait = isWait;
    }

    @Override
    protected void succeeded() {
        super.succeeded();
    }

    @Override
    protected void cancelled() {
        super.cancelled();
    }

    @Override
    protected void failed() {
        super.failed();
    }


    public void setMessage(String message){
        updateMessage(message);
    }
    @Override
    protected Integer call(){
        ResponseManager.response.clear();
        updateValue(1);
        try {
            LcProtocol protocol = new LcProtocol();
            byte[] packet = protocol.pack(commandNo, boardNo, boxList);
            SendManager.gateway.sendMessage(packet);
            if(isWait){

                LcResult result = ResponseManager.response.poll(WAIT_TIME, TimeUnit.SECONDS);
                if(result == null){
                    updateValue(-2);
                    return Integer.valueOf(-1);
                }
                else {
                    if(result.getErrorNo() == LcResult.SUCCESS){
                        updateValue(0);
                    }
                    else{
                        updateValue(-3);
                        return Integer.valueOf(-3);
                    }
                }
            }

            updateValue(0);
            return Integer.valueOf(0);
        }
        /*catch(ProtocolException e){
            updateMessage("打包失败:" + e.getErrorMessage());
            updateValue(-1);
        }*/
        catch (InterruptedException e){
            updateMessage("设备没有响应，请确认设备是否运行正常.");
            updateValue(-1);
        }
        catch (IOException e){
            updateMessage("发送数据包失败:" + e.getMessage());
            updateValue(-1);
        }
        catch (Exception e){
            updateValue(-1);
            e.printStackTrace();
        }
        return Integer.valueOf(-1);
    }
}
