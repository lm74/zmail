package com.zhy.smail.manager.view;

import com.zhy.smail.MainApp;
import com.zhy.smail.cabinet.entity.CabinetInfo;
import com.zhy.smail.cabinet.service.CabinetService;
import com.zhy.smail.common.controller.RootController;
import com.zhy.smail.config.LocalConfig;
import com.zhy.smail.manager.entity.DeliveryLog;
import com.zhy.smail.manager.entity.OpeningLog;
import com.zhy.smail.manager.service.DeliveryLogService;
import com.zhy.smail.manager.service.OpeningLogService;
import com.zhy.smail.restful.RestfulResult;
import com.zhy.smail.restful.RfFaultEvent;
import com.zhy.smail.restful.RfResultEvent;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by wenliz on 2017/3/2.
 */
public class OpeningLogController extends RootController implements Initializable {


    @FXML
    private Label lblTimer;


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

    @FXML
    private TableView<OpeningLog> logTable;
    @FXML
    private TableColumn<OpeningLog, String> tcOpeningTime;
    @FXML
    private TableColumn<OpeningLog, String> tcOpeningUser;

    @FXML
    private TableColumn<OpeningLog, Integer> tcBoxSequence;

    @FXML
    private TableColumn<OpeningLog, String> tcOpeningResult;
    private ObservableList logList;



    public void initialize(URL location, ResourceBundle resources) {
        tcOpeningTime.setCellValueFactory(new PropertyValueFactory("openingTime"));
        tcOpeningUser.setCellValueFactory(new PropertyValueFactory("openingUser"));
        tcBoxSequence.setCellValueFactory(new PropertyValueFactory("boxSequence"));
        tcOpeningResult.setCellValueFactory(new PropertyValueFactory("openingResult"));

        logList = FXCollections.observableArrayList();
        logTable.setItems(logList);

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
        OpeningLogService.listByCabinetId(currentCabinet.getCabinetId(), periodType, new RestfulResult() {
            @Override
            public void doResult(RfResultEvent event) {
                if(event.getData() != null){

                    List openingLogs = (List)event.getData();
                    logList.addAll(openingLogs);
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
}

