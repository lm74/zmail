package com.zhy.smail.task;

import com.zhy.smail.cabinet.entity.BoardEntry;
import com.zhy.smail.cabinet.entity.CabinetEntry;
import com.zhy.smail.common.utils.SystemUtil;
import com.zhy.smail.config.GlobalOption;
import com.zhy.smail.lcp.BoxEntry;
import com.zhy.smail.lcp.LcProtocol;
import com.zhy.smail.lcp.LcResult;
import com.zhy.smail.lcp.command.LcCommand;
import com.zhy.smail.manager.service.OpeningLogService;
import com.zhy.smail.restful.DefaultRestfulResult;
import javafx.concurrent.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by wenliz on 2017/2/25.
 */
public class OpenAllBoxTask  extends Task<Integer> {
    private CabinetEntry cabinet;

    public String getOpenedBoxNos() {
        return openedBoxNos;
    }

    public void setOpenedBoxNos(String openedBoxNos) {
        this.openedBoxNos = openedBoxNos;
    }

    private String openedBoxNos;
    private List<Integer> openedBoxList;

    public List<Integer> getOpenedBoxList() {
        return openedBoxList;
    }

    public void setOpenedBoxList(List<Integer> openedBoxList) {
        this.openedBoxList = openedBoxList;
    }

    public CabinetEntry getCabinet() {
        return cabinet;
    }

    public void setCabinet(CabinetEntry cabinet) {
        this.cabinet = cabinet;
    }

    public OpenAllBoxTask(CabinetEntry cabinet){
        this.cabinet = cabinet;
        openedBoxNos = "";
    }
    @Override
    protected Integer call() throws Exception {
        updateMessage("正在打开箱门...");
        if(!SystemUtil.canUse()){
            updateMessage("开箱失败，请联系厂家(9999)。");
            updateValue(-1);
            return -1;
        }
        openedBoxList = new ArrayList<>();
        List<BoardEntry> boards = cabinet.getBoards();
        for(int i=0; i<boards.size(); i++){
            BoardEntry boardEntry = boards.get(i);
            openBoardBoxes(boardEntry);
        }
        cabinet.setValue(1);
        if(openedBoxNos.length()>0){
            openedBoxNos = openedBoxNos.substring(1);
        }
        return 0;
    }

    public void openBoardBoxes(BoardEntry boardEntry){
        int[] boxList = boardEntry.getBoxNos();
        for (int i=0; i<boxList.length; i++) {
            int boxId = boxList[i];
            BoxEntry boxEntry = boardEntry.getBoxEntry(boxId);
            updateMessage("正在打开箱门("+(boxEntry.getSequence())+")...");
            int[] boxes = new int[]{boxId};
            ResponseManager.response.clear();
            try {
                LcProtocol protocol = new LcProtocol();
                byte[] packet = protocol.pack(LcCommand.OPEN_BOX, boardEntry.getBoardNo(), boxes);
                SendManager.gateway.sendMessage(packet);

                LcResult result = ResponseManager.response.poll(ResponseManager.WAIT_SECONDS, TimeUnit.SECONDS);
                try {
                    Thread.sleep(100);
                } catch (Exception e) {

                }
                if (result == null) {
                    updateMessage("打开箱门("+(boxEntry.getSequence())+")失败！");
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {

                    }

                } else {
                    if (result.getErrorNo() == LcResult.SUCCESS) {
                        openedBoxNos += "," + boxEntry.getSequence();
                        openedBoxList.add(boxEntry.getSequence());
                        OpeningLogService.save(GlobalOption.currentUser.getUserId(),  boxEntry.getBoxId(), "开箱成功", new DefaultRestfulResult());
                        continue;
                    } else {
                        updateMessage("打开箱门("+(boxEntry.getSequence())+")失败！");
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {

                        }

                    }
                }

            } catch (InterruptedException e) {
                updateMessage("设备没有响应，请确认设备是否运行正常.");
            } catch (IOException e) {
                updateMessage("发送数据包失败:" + e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }
            OpeningLogService.save(GlobalOption.currentUser.getUserId(), boxEntry.getBoxId(), "开箱失败", new DefaultRestfulResult());
        }


    }
}