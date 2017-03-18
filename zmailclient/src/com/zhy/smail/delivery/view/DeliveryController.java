package com.zhy.smail.delivery.view;

import com.zhy.smail.MainApp;
import com.zhy.smail.common.controller.RootController;
import com.zhy.smail.config.GlobalOption;
import com.zhy.smail.manager.entity.DeliveryLog;
import com.zhy.smail.manager.service.DeliveryLogService;
import com.zhy.smail.restful.RestfulResult;
import com.zhy.smail.restful.RfFaultEvent;
import com.zhy.smail.restful.RfResultEvent;
import com.zhy.smail.user.entity.UserInfo;
import com.zhy.smail.user.view.UserViewController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by wenliz on 2017/1/14.
 */
public class DeliveryController extends RootController implements Initializable{
    @FXML
    private Label lblTimer;
    @FXML
    private Region topLeft;
    @FXML
    private Region topRight;
    @FXML
    private Button mailButton;
    @FXML
    private Button packetButton;
    @FXML
    private Button tomailButton;




    public void setApp(MainApp app) {
        this.app = app;
        app.createTimeout(lblTimer);
    }

    public void initialize(URL location, ResourceBundle resources) {
        HBox.setHgrow(topLeft, Priority.ALWAYS);
        HBox.setHgrow(topRight, Priority.ALWAYS);

        UserInfo user = GlobalOption.currentUser;
        /*if(user.getUserType() == UserInfo.DELIVERY){
            mailButton.setVisible(false);
            tomailButton.setVisible(false);
        }*/
    }

    @FXML
    public void onBackAction(ActionEvent event){
        app.goHome();
    }

    @FXML
    public void onPutdownAction(ActionEvent actionEvent) throws IOException {
        PutdownController controller =  app.goPutdown();
        if(controller !=null) {
            controller.checkBoxStatus();
        }
    }

    @FXML
    public void onRecordAction(ActionEvent actionEvent) throws  IOException{
        GlobalOption.parents.push("delivery");
        FXMLLoader fxmlLoader;

        fxmlLoader = new FXMLLoader(getClass().getResource("deliveryLog.fxml"));
        Parent root = fxmlLoader.load();
        DeliveryLogController controller = fxmlLoader.getController();
        controller.setApp(app);

        app.getRootScene().setRoot(root);
    }

    @FXML
    public void onUserView(ActionEvent event){
        app.goUserView();
        GlobalOption.parents.push("delivery");
    }

    @FXML void onPutmailAction(ActionEvent event){
        PutmailController controller = app.goPutmail();
        if(controller!=null) {
            controller.checkBoxStatus();
        }
    }

    @FXML void onOccupyBoxAction(ActionEvent event){
        app.goOccupyBox();
    }
}
