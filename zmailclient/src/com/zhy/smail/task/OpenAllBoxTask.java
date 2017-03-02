package com.zhy.smail.task;

import com.zhy.smail.cabinet.entity.BoardEntry;
import com.zhy.smail.cabinet.entity.BoxInfo;
import com.zhy.smail.cabinet.entity.CabinetEntry;
import com.zhy.smail.lcp.BoxEntry;
import com.zhy.smail.lcp.LcProtocol;
import com.zhy.smail.lcp.LcResult;
import com.zhy.smail.lcp.command.LcCommand;
import javafx.concurrent.Task;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by wenliz on 2017/2/25.
 */
public class OpenAllBoxTask  extends Task<Integer> {
    private CabinetEntry cabinet;

    public CabinetEntry getCabinet() {
        return cabinet;
    }

    public void setCabinet(CabinetEntry cabinet) {
        this.cabinet = cabinet;
    }

    public OpenAllBoxTask(CabinetEntry cabinet){
        this.cabinet = cabinet;
    }
    @Override
    protected Integer call() throws Exception {
        updateMessage("正在打开箱门...");
        List<BoardEntry> boards = cabinet.getBoards();
        for(int i=0; i<boards.size(); i++){
            BoardEntry boardEntry = boards.get(i);

            openBoardBoxes(boardEntry);
        }
        cabinet.setValue(1);
        return 0;
    }

    public void openBoardBoxes(BoardEntry boardEntry){
        int[] boxList = boardEntry.getBoxIds();
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
        }


    }
}