package com.zhy.smail.task;

import com.zhy.smail.lcp.LcResult;
import com.zhy.smail.restful.RestfulResult;
import com.zhy.smail.restful.RfFaultEvent;
import com.zhy.smail.restful.RfResultEvent;
import com.zhy.smail.user.entity.UserInfo;
import com.zhy.smail.user.service.UserService;
import javafx.concurrent.Task;
import javafx.scene.control.*;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.*;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.VerticalAlignment;
import jxl.read.biff.BiffException;
import jxl.write.*;
import jxl.write.Label;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by wenliz on 2017/2/21.
 */
public class ExportUserTask extends Task<Integer> {
    private File file;
    private Integer userType;
    private List<UserInfo> users;
    public static BlockingQueue<Integer> response = new LinkedBlockingQueue<Integer>();

    public ExportUserTask(File file,Integer userType){
        this.file = file;
        this.userType = userType;
    }
    @Override
    protected Integer call() throws Exception {
        response.clear();
        users = null;
        updateMessage("正在导出...");
        File realFile = createFile(file);
        if(realFile == null){
            return -1;
        }
        updateProgress(5, 100);
        String userTypes = this.userType.toString();
        if(userType == UserInfo.ADMIN){
            userTypes = UserInfo.ADMIN +"," + UserInfo.ADVANCED_ADMIN+","+UserInfo.FACTORY_USER;
        }
        try {
            UserService.listByUserTyeps(userTypes, new RestfulResult() {
                @Override
                public void doResult(RfResultEvent event) {
                    if(event.getResult() == RfResultEvent.OK){
                        users = (List<UserInfo>) event.getData();
                    }
                    try {
                        response.put(1);
                    }
                    catch (Exception e){

                    }
                }

                @Override
                public void doFault(RfFaultEvent event) {
                    try {
                        response.put(1);
                    }
                    catch (Exception e){

                    }
                }
            });
            Integer flag = response.poll(2, TimeUnit.SECONDS);
            if(flag==null || users == null){
                updateMessage("读取用户信息出错.");
                return -1;
            }
        }
        catch (InterruptedException e){

        }
        updateProgress(10, 100);
        return toExcel();
    }

    private File createFile(File file){
        try{
            file.createNewFile();
        }
        catch(Exception e){
            updateMessage("创建文件出错:" + e.getMessage());
            return null;
        }
        return file;
    }

    private Integer toExcel(){
        try {
            WritableWorkbook book = Workbook.createWorkbook(file);
            WritableSheet sheet = book.createSheet("用户",0);
            writeTitleCell(sheet);
            updateProgress(20, 100);
            writeRow(sheet);
            book.write();
            book.close();
            updateMessage("导出成功.共导出" + users.size()+"条记录.");
        }
        catch (IOException e){
            updateMessage("导出失败:" + e.getMessage());
        }
        catch (WriteException e){
            updateMessage("导出失败:" + e.getMessage());
        }
        return -1;
    }

    private void writeTitleCell(WritableSheet sheet) throws WriteException{
        WritableFont wfc = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, false);
        WritableCellFormat wcf = new WritableCellFormat(wfc);
        wcf.setAlignment(Alignment.CENTRE);
        wcf.setVerticalAlignment(VerticalAlignment.CENTRE);
        wcf.setBorder(Border.ALL, BorderLineStyle.THICK);
        int index = 0;
        if(userType == UserInfo.OWNER){
            Label buildingNo = new Label(0, 0,"栋", wcf);
            sheet.addCell(buildingNo);
            sheet.addCell(new Label(1, 0, "单元",wcf));
            sheet.addCell(new Label(2, 0, "楼层",wcf));
            sheet.addCell(new Label(3, 0, "房号",wcf));
            index = 3;
        }
        else if(userType == UserInfo.DELIVERY){
            sheet.addCell(new Label(0, 0, "投递员",wcf));
        }
        else {
            sheet.addCell(new Label(0, 0, "管理员",wcf));
        }
        sheet.addCell(new Label(index+1, 0, "手机",wcf));
        sheet.addCell(new Label(index+2, 0, "卡号1",wcf));
        sheet.addCell(new Label(index+3, 0, "卡号2",wcf));
        sheet.addCell(new Label(index+4, 0, "卡号3",wcf));
        sheet.addCell(new Label(index+5, 0, "卡号4",wcf));
        sheet.addCell(new Label(index+6, 0, "卡号5",wcf));
        sheet.addCell(new Label(index+7, 0, "卡号6",wcf));
        sheet.addCell(new Label(index+8, 0, "卡号7",wcf));
        sheet.addCell(new Label(index+9, 0, "卡号8",wcf));
        sheet.addCell(new Label(index+10, 0, "卡号9",wcf));
        sheet.addCell(new Label(index+11, 0, "卡号10",wcf));
        sheet.addCell(new Label(index+12, 0, "用户类别", wcf));

    }

    private  void writeRow(WritableSheet sheet) throws WriteException {

        //结果字体
        WritableCellFormat wcf = new WritableCellFormat();
        wcf.setAlignment(Alignment.LEFT);
        wcf.setShrinkToFit(true);
        wcf.setVerticalAlignment(VerticalAlignment.CENTRE);
        wcf.setBorder(Border.ALL, BorderLineStyle.THIN);
        wcf.setWrap(true);
        double step = 80/users.size();

        for(int i=0; i<users.size(); i++){
            UserInfo user = users.get(i);
            int index = 0;
            String typeName = "";
            if(userType == UserInfo.OWNER){
                Label buildingNo = new Label(0, i+1,user.getBuildingNo(), wcf);
                sheet.addCell(buildingNo);
                sheet.addCell(new Label(1, i+1, user.getUnitNo(),wcf));
                sheet.addCell(new Label(2, i+1, user.getFloorNo(),wcf));
                sheet.addCell(new Label(3, i+1, user.getRoomNo(),wcf));
                index = 3;
            }
            else if(userType == UserInfo.DELIVERY){
                sheet.addCell(new Label(0, i+1, user.getUserName(),wcf));
            }
            else {
                sheet.addCell(new Label(0, i+1, user.getUserName(),wcf));
            }
            sheet.addCell(new Label(index+1, i+1, user.getPhoneNo(),wcf));
            sheet.addCell(new Label(index+2, i+1, user.getCardNo1(),wcf));
            sheet.addCell(new Label(index+3, i+1, user.getCardNo2(),wcf));
            sheet.addCell(new Label(index+4, i+1, user.getCardNo3(),wcf));
            sheet.addCell(new Label(index+5, i+1, user.getCardNo4(),wcf));
            sheet.addCell(new Label(index+6, i+1, user.getCardNo5(),wcf));
            sheet.addCell(new Label(index+7, i+1, user.getCardNo6(),wcf));
            sheet.addCell(new Label(index+8, i+1, user.getCardNo7(),wcf));
            sheet.addCell(new Label(index+9, i+1, user.getCardNo8(),wcf));
            sheet.addCell(new Label(index+10, i+1, user.getCardNo9(),wcf));
            sheet.addCell(new Label(index+11, i+1, user.getCardNo10(),wcf));
            sheet.addCell(new Label(index+12, i+1, user.getUserTypeTitle(),wcf));
            updateProgress((step*i)+20, 100);
        }

        updateProgress(100, 100);

    }

    private String getString(Map<String, String> row, String title){
        String value = row.get(title);
        if(value != null){
            return value;
        }
        else{
            return "";
        }
    }



}
