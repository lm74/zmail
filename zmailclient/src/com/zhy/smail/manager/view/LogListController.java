package com.zhy.smail.manager.view;

import com.zhy.smail.MainApp;
import com.zhy.smail.cabinet.entity.CabinetEntry;
import com.zhy.smail.cabinet.entity.CabinetInfo;
import com.zhy.smail.cabinet.service.CabinetService;
import com.zhy.smail.common.controller.RootController;
import com.zhy.smail.component.SimpleDialog;
import com.zhy.smail.component.music.Speaker;
import com.zhy.smail.config.GlobalOption;
import com.zhy.smail.config.LocalConfig;
import com.zhy.smail.manager.entity.DeliveryLog;
import com.zhy.smail.manager.service.DeliveryLogService;
import com.zhy.smail.restful.RestfulResult;
import com.zhy.smail.restful.RfFaultEvent;
import com.zhy.smail.restful.RfResultEvent;
import com.zhy.smail.task.OpenAllBoxTask;
import com.zhy.smail.user.entity.UserInfo;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.Callback;

import java.net.URL;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by wenliz on 2017/1/17.
 */
public class LogListController extends RootController implements Initializable {

    private MainApp app;
    @FXML
    private Label lblTimer;
    @FXML
    private Label lblTitle;
    @FXML
    private Button pickupButton;
    private List<DeliveryLog> mailLogs;
    private List<DeliveryLog> packetLogs;
    public void setApp(MainApp app) {
        this.app = app;
        app.createTimeout(lblTimer);
    }

    @FXML
    private ChoiceBox cabinetList;
    private List<CabinetInfo> cabinets;
    private CabinetInfo currentCabinet;
    private int currentCabinetIndex = 0;
    private int periodType = 1;
    private int pickedup = 0;
    @FXML
    private HBox toggleContainer;

    public int getPickedup() {
        return pickedup;
    }

    public void setPickedup(int pickedup) {
        this.pickedup = pickedup;
        // 超时记录查询
        if(this.pickedup == 1){
            this.periodType = 0;
            toggleContainer.setVisible(false);
            tcPickupTime.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<DeliveryLog, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<DeliveryLog, String> param) {
                    Timestamp deliveryTime = param.getValue().getDeliveryTime();
                    long inteval =  System.currentTimeMillis() - deliveryTime.getTime();
                    Long value = Long.valueOf(inteval/1000/60/60 - GlobalOption.timeout.getIntValue());
                    return new ReadOnlyStringWrapper( value.toString());
                }
            });
            lblTitle.setText("超时记录");
            tcPickupTime.setText("超时时间（小时）");
        } else if(this.pickedup == 0){
            // 投取记录查询
            toggleContainer.setVisible(true);
        } else if(this.pickedup == 2){
            // 未取记录查询
            lblTitle.setText("未取记录");
            toggleContainer.setVisible(true);
            tcPickupTime.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<DeliveryLog, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<DeliveryLog, String> param) {
                    String value = "待取";
                    return new ReadOnlyStringWrapper( value.toString());
                }
            });
            tcPickupTime.setText("取件状态");

        }
        initCabinet();
    }

    @FXML
    private TableView<DeliveryLog> logTable;
    @FXML
    private TableColumn<DeliveryLog, String> tcDeliveryTime;
    @FXML
    private TableColumn<DeliveryLog, String> tcDeliveryMan;
    @FXML
    private TableColumn<DeliveryLog, String> tcDeliveryType;
    @FXML
    private TableColumn<DeliveryLog, Integer> tcBoxSequence;
    @FXML
    private TableColumn<DeliveryLog, String> tcPickupTime;
    @FXML
    private TableColumn<DeliveryLog, String> tcPickupUser;
    private ObservableList logList;



    public void initialize(URL location, ResourceBundle resources) {
        tcDeliveryTime.setCellValueFactory(new PropertyValueFactory("deliveryTime"));
        tcDeliveryMan.setCellValueFactory(new PropertyValueFactory("deliveryMan"));
        tcDeliveryType.setCellValueFactory(new PropertyValueFactory("deliveryType"));
        tcBoxSequence.setCellValueFactory(new PropertyValueFactory("boxSequence"));
        tcPickupTime.setCellValueFactory(new PropertyValueFactory("pickupTime"));
        tcPickupUser.setCellValueFactory(new PropertyValueFactory("pickupUser"));

        logList = FXCollections.observableArrayList();
        logTable.setItems(logList);


    }

    private void initCabinet(){
        CabinetService.listAll(new RestfulResult() {
            @Override
            public void doResult(RfResultEvent event) {
                if (event.getResult() != RfResultEvent.OK) return;

                cabinetList.getItems().removeAll();
                cabinets = (List<CabinetInfo>) event.getData();

                for (int i = 0; i < cabinets.size(); i++) {
                    CabinetInfo cabinetInfo = (CabinetInfo) cabinets.get(i);
                    if (cabinetInfo.getCabinetNo().equals(LocalConfig.getInstance().getLocalCabinet())) {
                        cabinetList.getItems().add("本柜");
                        currentCabinet = cabinetInfo;
                        currentCabinetIndex = i;
                    } else {
                        cabinetList.getItems().add(cabinetInfo.getCabinetNo());
                    }
                }
                cabinetList.getSelectionModel().select(currentCabinetIndex);
                cabinetList.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                        currentCabinetIndex = newValue.intValue();
                        currentCabinet = cabinets.get(currentCabinetIndex);
                        onRefreshAction();
                    }
                });
                onRefreshAction();
            }
            @Override
            public void doFault(RfFaultEvent event) {

            }
        });
    }

    @FXML
    private void onThisDateAction(ActionEvent event){
        this.periodType = DeliveryLog.THIS_DATE;
        onRefreshAction();
    }

    @FXML
    private void onThisWeekAction(ActionEvent event){
        this.periodType = DeliveryLog.THIS_WEEK;
        onRefreshAction();
    }

    @FXML
    private void onThisMonthAction(ActionEvent event){
        this.periodType = DeliveryLog.THIS_MONTH;
        onRefreshAction();
    }
    @FXML
    private void onOneMonthAction(ActionEvent event){
        this.periodType = DeliveryLog.RECENT_ONE_MONTH;
        onRefreshAction();
    }
    @FXML
    private void onThreeMonthAction(ActionEvent event){
        this.periodType = DeliveryLog.RECENT_THREE_MONTH;
        onRefreshAction();
    }
    @FXML
    private void onAllLogAction(ActionEvent event){
        this.periodType = 0;
        onRefreshAction();
    }

    private void onRefreshAction(){
        logList.clear();
        DeliveryLogService.listByCabinetId(currentCabinet.getCabinetId(), periodType,pickedup, new RestfulResult() {
            @Override
            public void doResult(RfResultEvent event) {
                if(event.getData() != null){

                    List deliveryLogs = (List)event.getData();
                    logList.addAll(deliveryLogs);
                }
            }

            @Override
            public void doFault(RfFaultEvent event) {

            }
        });
    }

    @FXML
    private void onBackAction(ActionEvent event){
        app.goQueryRecord();
    }

    @FXML
    private void onPickupAction(ActionEvent event){
        //取出物品触发事件
        //只有选择本柜的时候才会触发取出物品事件
        if (!LocalConfig.getInstance().getLocalCabinet().equals(currentCabinet.getCabinetNo())) {
            SimpleDialog.showMessageDialog(app.getRootStage(),
                    "请到信包所在柜取件！", "");
            return;
        } else {
            DeliveryLog deliveryLogInfo = getSelectedDeliveryLogInfo();
            if (deliveryLogInfo == null) {
                SimpleDialog.showMessageDialog(app.getRootStage(), "请选择一条记录.", "");
                return;
            }else {
                String message = "你确认要取出箱包中的物品吗？";
                SimpleDialog.Response response = SimpleDialog.showConfirmDialog(app.getRootStage(), message, "确认");
                if (response == SimpleDialog.Response.NO) {
                    return;
                }else {
                    // 0代表的是打开信件箱，1代表的是打开包裹箱
                    getDeliveryLogByOwner(deliveryLogInfo.getPickupUser());
                    if (DeliveryLog.DELIVERY_TYPE_MAIL == deliveryLogInfo.getDeliveryType()) {
                        openMailDoorAction(event);
                    } else {
                        openPacketDoorAction(event);
                    }
                }
            }
            onRefreshAction();
        }
    }

    // 选中一条table的记录
    private DeliveryLog getSelectedDeliveryLogInfo() {
        DeliveryLog deliveryLog;
        deliveryLog = logTable.getSelectionModel().getSelectedItem();
        return deliveryLog;
    }

    private void getDeliveryLogByOwner(UserInfo userInfo) {
        mailLogs = new ArrayList<DeliveryLog>();
        packetLogs = new ArrayList<>();
        DeliveryLogService.listByOwner(GlobalOption.currentCabinet.getCabinetId(), userInfo.getUserId(), 2, new RestfulResult() {
            @Override
            public void doResult(RfResultEvent event) {
                if (event.getData() != null) {
                    List<DeliveryLog> newLogs = (List<DeliveryLog>) event.getData();
                    for (int i = 0; i < newLogs.size(); i++) {
                        DeliveryLog log = newLogs.get(i);
                        Integer boxNo = log.getBoxInfo().getSequence();
                        if (log.getDeliveryType().equals(DeliveryLog.DELIVERY_TYPE_MAIL)) {
                            mailLogs.add(log);
                        } else {
                            packetLogs.add(log);
                        }
                    }
                }
            }

            @Override
            public void doFault(RfFaultEvent event) {

            }
        });
    }

    private void openMailDoorAction(ActionEvent event) {
        CabinetEntry cabinetEntry = getCabinetEntry(mailLogs);
        openAllDoor(cabinetEntry, mailLogs);
    }

    private void openPacketDoorAction(ActionEvent event){
        CabinetEntry cabinetEntry = getCabinetEntry(packetLogs);
        openAllDoor(cabinetEntry, packetLogs);
    }

    private CabinetEntry getCabinetEntry(List<DeliveryLog> logs) {
        CabinetEntry cabinetEntry = new CabinetEntry();
        for(int i=0; i<logs.size(); i++){
            DeliveryLog log = logs.get(i);
            cabinetEntry.addBox(log.getBoxInfo());
        }
        return cabinetEntry;
    }

    private void openAllDoor(CabinetEntry cabinetEntry, List<DeliveryLog> logs){
        OpenAllBoxTask task = new OpenAllBoxTask(cabinetEntry);
        task.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                if (newValue != 0) return;
                if (task.getOpenedBoxNos().length() == 0) {
                    SimpleDialog.showMessageDialog(app.getRootStage(), "开箱失败，请重试或联系管理员。", "");
                    return;
                } else {
                    SimpleDialog.showAutoCloseInfo(app.getRootStage(),"箱门已经打开，请您一次性取出所有物品后关好箱门。");
                    Speaker.openBoxSound();
                    Speaker.doorOpened();
                }
                savePickup(logs, task);
            }
        });
        SimpleDialog.showDialog(app.getRootStage(), task, "", "");
    }

    private void savePickup(List<DeliveryLog> logs, OpenAllBoxTask task) {
        for (int i = 0; i < logs.size(); i++) {
            DeliveryLog log = logs.get(i);
            for (int ii = 0; ii < task.getOpenedBoxList().size(); ii++) {
                Integer sequence = task.getOpenedBoxList().get(ii);
                if (log.getBoxInfo().getSequence().equals(sequence)) {
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
        }
    }
}

