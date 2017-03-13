package com.zhy.smail.manager.view;

import com.zhy.smail.MainApp;
import com.zhy.smail.common.controller.RootController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Administrator on 2017/3/11.
 */
public class QueryRecordController extends RootController implements Initializable {
    @FXML
    private Label lblTimer;
    @FXML
    private ToolBar topToolBar;
    @FXML
    private Region topLeft;
    @FXML
    private Region topRight;

    public void setApp(MainApp app) {
        this.app = app;
        app.createTimeout(lblTimer);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources){
        HBox.setHgrow(topLeft, Priority.ALWAYS);
        HBox.setHgrow(topRight, Priority.ALWAYS);

    }

    @FXML
    private void onBackAction(ActionEvent event){
        app.goManager();
    }

    @FXML
    public void onOpeningLogAction(ActionEvent event){
        app.goOpeningLog();
    }

    @FXML
    private void onLogListAction(ActionEvent event){
        LogListController controller = app.goLogList();
        controller.setPickedup(0);
    }
    @FXML
    private void onTimeoutAction(ActionEvent event){
        LogListController controller = app.goLogList();
        controller.setPickedup(1);
    }


}
