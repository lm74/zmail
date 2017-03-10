package com.zhy.smail.task;

import com.zhy.smail.cabinet.entity.BoxInfo;
import com.zhy.smail.common.utils.SystemUtil;
import com.zhy.smail.component.SimpleDialog;
import com.zhy.smail.config.GlobalOption;
import com.zhy.smail.lcp.LcProtocol;
import com.zhy.smail.lcp.LcResult;
import com.zhy.smail.lcp.command.LcCommand;
import com.zhy.smail.manager.service.OpeningLogService;
import com.zhy.smail.restful.DefaultRestfulResult;
import com.zhy.smail.restful.RestfulResult;
import com.zhy.smail.restful.RfFaultEvent;
import com.zhy.smail.restful.RfResultEvent;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by wenliz on 2017/2/23.
 */
public class OpenBoxTask extends Task<Integer> {
    private BoxInfo box;
    private String message;
    public OpenBoxTask(BoxInfo box, String message){
        this.box = box;
        this.message = message;
    }
    @Override
    protected Integer call() throws Exception {
        int[] boxList = new int[]{box.getControlSequence()};
        updateMessage(message);
        ResponseManager.response.clear();
        try {
            if(!SystemUtil.canUse()){
                updateMessage("开箱失败，请联系厂家(9999)。");
                updateValue(-1);
                return -1;
            }
            LcProtocol protocol = new LcProtocol();
            byte[] packet = protocol.pack(LcCommand.OPEN_BOX, box.getControlCardId(), boxList);
            SendManager.gateway.sendMessage(packet);

            LcResult result = ResponseManager.response.poll(ResponseManager.WAIT_SECONDS, TimeUnit.SECONDS);
            if (result == null) {
                updateMessage("开箱失败，请重试。");
                updateValue(-1);
            } else {
                if (result.getErrorNo() == LcResult.SUCCESS) {
                    updateValue(0);
                    OpeningLogService.save(GlobalOption.currentUser.getUserId(), box.getBoxId(), "开箱成功", new DefaultRestfulResult());
                    return 0;
                } else {
                    updateMessage("开箱失败，请重试。");
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
        OpeningLogService.save(GlobalOption.currentUser.getUserId(), box.getBoxId(), "开箱失败", new DefaultRestfulResult());
        return -1;
    }

}
