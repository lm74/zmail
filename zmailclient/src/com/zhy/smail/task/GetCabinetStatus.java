package com.zhy.smail.task;

import com.zhy.smail.cabinet.entity.BoardEntry;
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
 * Created by wenliz on 2017/2/24.
 */
public class GetCabinetStatus extends Task<Integer> {
    private CabinetEntry cabinet;

    public CabinetEntry getCabinet() {
        return cabinet;
    }

    public void setCabinet(CabinetEntry cabinet) {
        this.cabinet = cabinet;
    }

    public GetCabinetStatus(CabinetEntry cabinet){
        this.cabinet = cabinet;
    }
    @Override
    protected Integer call() throws Exception {
        updateMessage("正在检查箱门状态...");
        List<BoardEntry> boards = cabinet.getBoards();
        for(int i=0; i<boards.size(); i++){
            BoardEntry boardEntry = boards.get(i);
            updateMessage("正在检查箱门状态("+(i+1)+")...");
            getBoardStatus(boardEntry);
            try {
                Thread.sleep(100);
            }
            catch (Exception e){

            }

        }
        cabinet.setValue(1);
        return 0;
    }

    public void getBoardStatus(BoardEntry boardEntry){
        int[] boxList = boardEntry.getBoxNos();
        ResponseManager.response.clear();
        try {
            LcProtocol protocol = new LcProtocol();
            byte[] packet = protocol.pack(LcCommand.READ_BOX_STATUS, boardEntry.getBoardNo(), boxList);
            SendManager.gateway.sendMessage(packet);

            LcResult result = ResponseManager.response.poll(ResponseManager.WAIT_SECONDS, TimeUnit.SECONDS);
            if (result == null) {
                return;
            } else {
                if (result.getErrorNo() == LcResult.SUCCESS ) {
                    if(result.getData().length>0) {
                        Object[] status = result.getData();
                        for(int i=0; i<status.length; i++){
                            BoxEntry entry = (BoxEntry) status[i];
                            boardEntry.addEntry(entry);
                        }
                    }
                } else {
                    return;
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

    }
}
