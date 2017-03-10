package com.zhy.smail.pickup.view;

import com.zhy.smail.MainApp;
import com.zhy.smail.cabinet.entity.BoxInfo;
import com.zhy.smail.cabinet.entity.CabinetEntry;
import com.zhy.smail.cabinet.entity.CabinetNode;
import com.zhy.smail.component.SimpleDialog;
import com.zhy.smail.component.music.Speaker;
import com.zhy.smail.config.GlobalOption;
import com.zhy.smail.lcp.LcProtocol;
import com.zhy.smail.lcp.LcResult;
import com.zhy.smail.lcp.command.LcCommand;
import com.zhy.smail.manager.entity.DeliveryLog;
import com.zhy.smail.manager.service.DeliveryLogService;
import com.zhy.smail.restful.RestfulResult;
import com.zhy.smail.restful.RfFaultEvent;
import com.zhy.smail.restful.RfResultEvent;
import com.zhy.smail.task.OpenAllBoxTask;
import com.zhy.smail.task.ResponseManager;
import com.zhy.smail.task.SendManager;
import com.zhy.smail.user.entity.UserInfo;
import com.zhy.smail.user.view.UserViewController;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;

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
    private Label lblBoxNumber;
    @FXML
    private Button openDoorButton;
    @FXML
    private Label lblOpenMessage;
    @FXML
    private Label lblNotify;
    @FXML
    private Label lblBoxList;

    private MainApp app;
    private List<DeliveryLog> logs;
    private boolean opened;

    public void setApp(MainApp app) {
        this.app = app;
        app.createTimeout(lblTimer);
    }

    public void initialize(URL location, ResourceBundle resources) {
        setBoxNumber(0, "");
        lblOpenMessage.setVisible(false);
        HBox.setHgrow(topLeft, Priority.ALWAYS);
        HBox.setHgrow(topRight, Priority.ALWAYS);
        UserInfo user = GlobalOption.currentUser;

        logs = new ArrayList<DeliveryLog>();
        DeliveryLogService.listByOwner(GlobalOption.currentCabinet.getCabinetId(), GlobalOption.currentUser.getUserId(), new RestfulResult() {
            @Override
            public void doResult(RfResultEvent event) {

                logs.clear();

                Set<Integer> boxSetNos = new HashSet<Integer>();
                if(event.getData()!=null){

                    List<DeliveryLog> newLogs = (List<DeliveryLog>) event.getData();
                    logs.addAll(newLogs);
                    for(int i=0; i<newLogs.size(); i++){
                        DeliveryLog log = newLogs.get(i);
                        boxSetNos.add(log.getBoxInfo().getSequence());
                    }
                }
                final String boxNos= boxSetNos.toString();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        setBoxNumber(logs.size(), boxNos);
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
                if(event.getResult() == RfResultEvent.OK && event.getData() != null) return;

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
                    lblNotify.setText("您还有信包在如下柜中：" + nodeMessage);
                }
            }

            @Override
            public void doFault(RfFaultEvent event) {

            }
        });


    }

    private void setBoxNumber(Integer boxNumber, String boxNos){

        if(boxNumber == 0){
            lblBoxNumber.setText("您有0信包。");
            openDoorButton.setDisable(true);
            lblBoxList.setVisible(false);
            Speaker.noPacket();
        }
        else{
            lblBoxNumber.setText("您有"+ boxNumber.toString()+"信包。箱号如下：");
            lblBoxList.setVisible(true);
            openDoorButton.setDisable(false);
        }
    }
    private CabinetEntry getCabinetEntry() {
        CabinetEntry cabinetEntry = new CabinetEntry();
        for(int i=0; i<logs.size(); i++){
            DeliveryLog log = logs.get(i);
            cabinetEntry.addBox(log.getBoxInfo());
        }
        return cabinetEntry;
    }

    @FXML
    private void openDoorAction(ActionEvent event) {
        CabinetEntry cabinetEntry = getCabinetEntry();

        OpenAllBoxTask task = new OpenAllBoxTask(cabinetEntry);
        task.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                if (newValue != 0) return;

                lblOpenMessage.setVisible(true);
                lblOpenMessage.setText(task.getOpenedBoxNos()+"号箱门已经打开，请取出物品后关好箱门。");
                Speaker.openBoxSound();
                Speaker.doorOpened();
                for (int i = 0; i < logs.size(); i++) {
                    DeliveryLog log = logs.get(i);
                    DeliveryLogService.pickup(log.getLogId(), GlobalOption.currentUser.getUserId(), 0, new
                            RestfulResult() {
                                @Override
                                public void doResult(RfResultEvent event) {

                                }

                                @Override
                                public void doFault(RfFaultEvent event) {

                                }
                            });
                }

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


}
