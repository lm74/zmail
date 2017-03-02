package com.zhy.smail.task;

import com.zhy.smail.cabinet.entity.BoxInfo;
import com.zhy.smail.lcp.BoxEntry;
import com.zhy.smail.lcp.LcProtocol;
import com.zhy.smail.lcp.LcResult;
import com.zhy.smail.lcp.command.LcCommand;
import javafx.concurrent.Task;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by wenliz on 2017/2/23.
 */
public class GetBoxStatusTask extends Task<Integer> {
    private BoxInfo box;

    public GetBoxStatusTask(BoxInfo box){
        this.box = box;
    }
    @Override
    protected Integer call() throws Exception {

        int[] boxList = new int[]{box.getControlSequence()};
        updateMessage("正在检查箱门状态...");
        ResponseManager.response.clear();
        try {
            LcProtocol protocol = new LcProtocol();
            byte[] packet = protocol.pack(LcCommand.READ_BOX_STATUS, box.getControlCardId(), boxList);
            SendManager.gateway.sendMessage(packet);

            LcResult result = ResponseManager.response.poll(ResponseManager.WAIT_SECONDS, TimeUnit.SECONDS);
            if (result == null) {
                updateMessage("读取箱门失败，请重试或联系管理员。");
                updateValue(-1);
            } else {
                if (result.getErrorNo() == LcResult.SUCCESS ) {
                    if(result.getData().length>0) {
                        BoxEntry entry = (BoxEntry) result.getData()[0];
                        updateValue(entry.getStatus());
                        return entry.getStatus();
                    }
                } else {
                    updateMessage("读取箱门失败，请重试或联系管理员。");
                    updateValue(-1);
                }
            }

        }
        catch (InterruptedException e){
            updateMessage("设备没有响应，请确认设备是否运行正常.");
        }
        catch (IOException e){
            updateMessage("发送数据包失败:" + e.getMessage());
        }
        catch (Exception e){
            e.printStackTrace();
        }
        updateValue(-1);
        return -1;
    }

}
