package com.zhy.smail.pickup.view;

import com.zhy.smail.MainApp;
import com.zhy.smail.cabinet.entity.CabinetEntry;
import com.zhy.smail.cabinet.entity.CabinetNode;
import com.zhy.smail.component.SimpleDialog;
import com.zhy.smail.component.music.Speaker;
import com.zhy.smail.config.GlobalOption;
import com.zhy.smail.manager.entity.DeliveryLog;
import com.zhy.smail.manager.service.DeliveryLogService;
import com.zhy.smail.restful.RestfulResult;
import com.zhy.smail.restful.RfFaultEvent;
import com.zhy.smail.restful.RfResultEvent;
import com.zhy.smail.task.OpenAllBoxTask;
import com.zhy.smail.user.entity.UserInfo;
import com.zhy.smail.user.view.UserViewController;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Paint;

import java.net.URL;
import java.util.*;

/**
 * Created by wenliz on 2017/1/14.
 */
public class PickupController  implements Initializable {
    @FXML
    private Label lblTimer;
    @FXML
    private Region topLeft;
    @FXML
    private Region topRight;
    @FXML
    private Button openPacketDoorButton;
    @FXML
    private Button openMailDoorButton;
    @FXML
    private Label lblOpenMessage;
    @FXML
    private Label lblNotify;
    @FXML
    private Label lblMailBoxNumber;
    @FXML
    private Label lblPacketBoxNumber;
    @FXML
    private Label lblUserInfo;
    private MainApp app;
    private List<DeliveryLog> mailLogs;
    private List<DeliveryLog> packetLogs;
    private boolean opened;

    public void setApp(MainApp app) {
        this.app = app;
        app.createTimeout(lblTimer);
    }

    public void initialize(URL location, ResourceBundle resources) {
        mailLogs = new ArrayList<DeliveryLog>();
        packetLogs = new ArrayList<>();

        setBoxNumber("", "");
        lblOpenMessage.setVisible(false);
        HBox.setHgrow(topLeft, Priority.ALWAYS);
        HBox.setHgrow(topRight, Priority.ALWAYS);
        UserInfo user = GlobalOption.currentUser;
        if(UserInfo.OWNER == user.getUserType()){
            lblUserInfo.setText(user.getBuildingNo()
                    + "栋" + user.getUnitNo() + "单元"
                    + user.getRoomNo() + "（房号:"
                    + user.getBuildingNo()
                    + user.getUnitNo()
                    + user.getRoomNo()+" )");
        }else {
            lblUserInfo.setText("");
        }
        DeliveryLogService.listByOwner(GlobalOption.currentCabinet.getCabinetId(), GlobalOption.currentUser.getUserId(),0, new RestfulResult() {
            @Override
            public void doResult(RfResultEvent event) {
                mailLogs.clear();
                packetLogs.clear();
                Set<Integer> mailBoxSetNos = new HashSet<Integer>();
                Set<Integer> packetBoxSetNos = new HashSet<Integer>();
                String mailBoxNos="";
                String packetBoxNos = "";
                if(event.getData()!=null){
                    List<DeliveryLog> newLogs = (List<DeliveryLog>) event.getData();
                    for(int i=0; i<newLogs.size(); i++){
                        DeliveryLog log = newLogs.get(i);
                        Integer boxNo = log.getBoxInfo().getSequence();
                        if(log.getDeliveryType().equals(DeliveryLog.DELIVERY_TYPE_MAIL)){
                            mailLogs.add(log);
                            if(!mailBoxSetNos.contains(boxNo)) {
                                mailBoxSetNos.add(boxNo);
                                mailBoxNos += "," + boxNo+"号箱";
                            }

                        }
                        else{
                            packetLogs.add(log);
                            if(!packetBoxSetNos.contains(boxNo)) {
                                packetBoxSetNos.add(boxNo);
                                packetBoxNos += ","+boxNo+"号箱";
                            }
                        }
                    }
                }
                final String finalMailBoxNos = mailBoxSetNos.size()==0? "" : mailBoxNos.substring(1);
                final String finalPacketBoxNos = packetBoxSetNos.size() == 0 ? "": packetBoxNos.substring(1);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        setBoxNumber(finalMailBoxNos, finalPacketBoxNos);
                        if(mailLogs.size() == 0 && packetLogs.size()==0){
                            Speaker.noPacket();
                        }
                    }
                });
            }

            @Override
            public void doFault(RfFaultEvent event) {

            }
        });
        DeliveryLogService.listAllByOwner(GlobalOption.currentUser.getUserId(), new RestfulResult() {
            @Override
            public void doResult(RfResultEvent event) {
                if(event.getResult() != RfResultEvent.OK && event.getData() == null) return;//修改判断，显示其他柜信件

                List<CabinetNode> nodes = (List<CabinetNode>) event.getData();
                String nodeMessage = "";
                for(int i=0; i<nodes.size(); i++){
                    CabinetNode node = nodes.get(i);
                    if(!node.getCabinetId().equals(GlobalOption.currentCabinet.getCabinetId())){
                        nodeMessage += "," + node.getCabinetNo().toString();
                    }

                }
                if(!nodeMessage.equals("")) {
                    nodeMessage = nodeMessage.substring(1);
                    lblNotify.setText("您还有信包在" + nodeMessage+"号柜中，请前往所在柜领取！");
                }
            }

            @Override
            public void doFault(RfFaultEvent event) {

            }
        });


    }

    private void setBoxNumber(String mailBoxNos, String packetBoxNos){
        if(mailLogs.size() == 0 && packetLogs.size()==0){
//            Speaker.noPacket();
        }

        if(mailLogs.size() ==0){
            lblMailBoxNumber.setText("您有0个信件。");
            openMailDoorButton.setDisable(true);


        }
        else{
            lblMailBoxNumber.setText("您有" + mailLogs.size()+"个信件在：" + mailBoxNos);
            openMailDoorButton.setDisable(false);
        }

        if(packetLogs.size() == 0){
            lblPacketBoxNumber.setText("您有0个包裹。");
            openPacketDoorButton.setDisable(true);

        }
        else{
            lblPacketBoxNumber.setText("您有"+ packetLogs.size()+"个包裹在：" + packetBoxNos);
            openPacketDoorButton.setDisable(false);
        }
    }
    private CabinetEntry getCabinetEntry(List<DeliveryLog> logs) {
        CabinetEntry cabinetEntry = new CabinetEntry();
        for(int i=0; i<logs.size(); i++){
            DeliveryLog log = logs.get(i);
            cabinetEntry.addBox(log.getBoxInfo());
        }
        return cabinetEntry;
    }

    @FXML
    private void openMailDoorAction(ActionEvent event) {
        CabinetEntry cabinetEntry = getCabinetEntry(mailLogs);
        openAllDoor(cabinetEntry, mailLogs);
    }

    @FXML
    private void openPacketDoorAction(ActionEvent event){
        CabinetEntry cabinetEntry = getCabinetEntry(packetLogs);
        openAllDoor(cabinetEntry, packetLogs);
    }

    private void openAllDoor(CabinetEntry cabinetEntry, List<DeliveryLog> logs){
        OpenAllBoxTask task = new OpenAllBoxTask(cabinetEntry);
        task.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                if (newValue != 0) return;

                lblOpenMessage.setVisible(true);
                if (task.getOpenedBoxNos().length() == 0) {
                    lblOpenMessage.setText("开箱失败，请重试或联系管理员");
                } else {
                    lblOpenMessage.setText(task.getOpenedBoxNos() + "箱门已经打开，请您一次性取出所有物品后关好箱门。");
                    Speaker.openBoxSound();
                    Speaker.doorOpened();
                }

                savePickup(logs, task);
            }
        });
        SimpleDialog.showDialog(app.getRootStage(), task, "", "");

    }

    @FXML
    public void onBackAction(ActionEvent event){
        app.goHome();
    }

    @FXML
    public void onUserView(ActionEvent event){
        UserViewController controller = app.goUserView();
        GlobalOption.parents.push("pickup");
    }

    @FXML
    public void onPickupLogAction(ActionEvent event){
        app.goPickupLog();
    }

    private void savePickup(List<DeliveryLog> logs, OpenAllBoxTask task) {
        for (int i = 0; i < logs.size(); i++) {
            DeliveryLog log = logs.get(i);
            for(int ii=0; ii<task.getOpenedBoxList().size(); ii++){
                Integer sequence = task.getOpenedBoxList().get(ii);
                if(log.getBoxInfo().getSequence().equals(sequence)){
                    DeliveryLogService.pickup(log.getLogId(), GlobalOption.currentUser.getUserId(), 0, new
                            RestfulResult() {
                                @Override
                                public void doResult(RfResultEvent event) {}

                                @Override
                                public void doFault(RfFaultEvent event) { }
                            });

                }
            }
        }
    }
}

