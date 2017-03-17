package com.zhy.smail.task;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Import;
import com.zhy.smail.common.utils.KeySecurity;
import com.zhy.smail.restful.RestfulResult;
import com.zhy.smail.restful.RfFaultEvent;
import com.zhy.smail.restful.RfResultEvent;
import com.zhy.smail.user.entity.UserInfo;
import com.zhy.smail.user.service.UserService;
import javafx.concurrent.Task;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wenliz on 2017/2/20.
 */
public class ImportUserTask extends Task<Integer> {
    private File file;
    private Integer userType;
    public ImportUserTask(File file,Integer userType){
        this.file = file;
        this.userType = userType;
    }
    @Override
    protected Integer call() throws Exception {
        updateMessage("正在导入...");
        List<HashMap> excel = readExcelFile(file);
        if(excel == null){
            updateMessage("读取文件出错，请确认文件格式是否正确.");
            return -1;
        }
        updateMessage("正在保存信息...");
        Integer count = writeToDb(excel);
        updateMessage("成功导入" + count+"条记录.");
        return -1;
    }

    private Integer writeToDb(List<HashMap> excel){
        double  step = 80/excel.size();
        int count=0;
        for(int i=1; i<excel.size(); i++){
            Map<String, String> row = excel.get(i);
            UserInfo user = getUser(row);
            updateProgress((step*i-1)+20, 100);
            if(user == null) continue;
            else {
                user.setPassword(KeySecurity.encrypt("123456"));
            }

            UserService.save(user, new RestfulResult() {
                @Override
                public void doResult(RfResultEvent event) {

                }

                @Override
                public void doFault(RfFaultEvent event) {

                }
            });
            count++;
            try {
                Thread.sleep(200);
            }
            catch (Exception e){

            }

        }
        return count;
    }

    private UserInfo getUser(Map<String, String> row){
        UserInfo user= new UserInfo();
        if(userType == UserInfo.OWNER){
            user.setBuildingNo(getString(row, "栋"));
            user.setUnitNo(getString(row, "单元"));
            user.setFloorNo(getString(row, "楼层"));
            user.setRoomNo(getString(row, "房号"));
            user.setUserName(user.getBuildingNo()+user.getUnitNo()+user.getFloorNo()+user.getRoomNo());
        }
        else if(userType == UserInfo.DELIVERY){
            user.setUserName(getString(row, "投递员"));
        }
        else{
            user.setUserName(getString(row, "管理员"));
        }
        if(user.getUserName() == null || user.getUserName().equals("")){
            user.setUserName(getString(row, "用户"));
            if(user.getUserName() == null || user.getUserName().equals("")){
                user.setUserName(getString(row, "用户名称"));
            }
        }
        if(user.getUserName() == null || user.getUserName().equals("")){
            return null;
        }

        String typeName = row.get("用户类别");
        if(typeName == null) {
            user.setUserType(userType);
        }
        else{
            Integer newType = user.getUserType(typeName);
            if(newType== null){
                user.setUserType(userType);
            }
            else {
                user.setUserType(newType);
            }
        }
        user.setPhoneNo(getString(row, "手机"));
        if(user.getPhoneNo().equals("")){
            user.setPhoneNo(getString(row, "电话"));
        }
        user.setCardNo1(getString(row, "卡号1"));
        user.setCardNo2(getString(row, "卡号2"));
        user.setCardNo3(getString(row, "卡号3"));
        user.setCardNo4(getString(row, "卡号4"));
        user.setCardNo5(getString(row, "卡号5"));
        user.setCardNo6(getString(row, "卡号6"));
        user.setCardNo7(getString(row, "卡号7"));
        user.setCardNo8(getString(row, "卡号8"));
        user.setCardNo9(getString(row, "卡号9"));
        user.setCardNo10(getString(row, "卡号10"));
        return user;
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


    private List<HashMap> readExcelFile(File file) {
        Workbook wb = null;
        try {
            updateProgress(0, 100);
            wb = Workbook.getWorkbook(file);
            Sheet st = wb.getSheet(0);
            updateProgress(5, 100);
            int rowCount = st.getRows();
            int colCount = st.getColumns();

            String content;
            ArrayList<HashMap> list = new ArrayList<HashMap>();
            HashMap title = new HashMap();
            for (int j = 0; j < colCount; j++) {
                content = "";
                Cell t = st.getCell(j, 0);
                content = t.getContents();
                if(content !=  null){
                    content = content.toLowerCase();
                }
                title.put(j, content);
            }
            list.add(title);
            updateProgress(10, 100);

            for (int i = 1; i < rowCount; i++) {
                HashMap row = new HashMap();
                for (int j = 0; j < colCount; j++) {
                    content = "";
                    Cell data = st.getCell(j, i);
                    if (data != null) {
                        content = data.getContents();
                        row.put(title.get(j), content);
                    }
                }
                list.add(row);
            }
            updateProgress(20, 100);
            int step = rowCount/80;


            wb.close();
            return list;

        }
        catch (BiffException e){

        }
        catch (IOException e){

        }
        finally {
            if(wb !=null ){
                wb.close();
            }
        }
        return null;
    }
}
