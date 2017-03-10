package com.zhy.smail.manager.view;

import com.sun.xml.internal.ws.api.config.management.policy.ManagementAssertion;
import com.zhy.smail.MainApp;
import com.zhy.smail.cabinet.view.BoxListController;
import com.zhy.smail.cabinet.view.CabinetListController;
import com.zhy.smail.config.GlobalOption;
import com.zhy.smail.setting.view.SettingController;
import com.zhy.smail.user.entity.UserInfo;
import com.zhy.smail.user.view.UserListController;
import com.zhy.smail.user.view.UserViewController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by wenliz on 2017/1/16.
 */
public class ManagerController implements Initializable{
    @FXML
    private Label lblTimer;
    @FXML
    private Button userListButton;
    @FXML
    private  Button settingButton;
    @FXML
    private Button exitButton;
    @FXML
    private Button cabinetButton;
    @FXML
    private Button openingButton;
    @FXML
    private Button boxButton;
    @FXML
    private Button deliveryButton;

    private MainApp app;
    private Integer userType;

    public void setApp(MainApp app) {
        this.app = app;
        app.createTimeout(lblTimer);
    }

    public void initialize(URL location, ResourceBundle resources){
        UserInfo user = GlobalOption.currentUser;
        userType = user.getUserType();
        if(GlobalOption.runMode == 1){
            userListButton.setVisible(false);
            cabinetButton.setVisible(false);
            openingButton.setVisible(false);
            deliveryButton.setVisible(false);
            boxButton.setVisible(false);
        }
        if(user.getUserType() == UserInfo.FACTORY_USER) return;


        if(userType == UserInfo.ADMIN){
            userListButton.setText("帐号信息");
            settingButton.setVisible(false);
            exitButton.setVisible(false);
            cabinetButton.setVisible(false);
        }
        if(userType == UserInfo.ADVANCED_ADMIN){
            cabinetButton.setVisible(false);
        }
    }


    @FXML
    public void onUserListAction(ActionEvent actionEvent) throws IOException{
        if(userType == UserInfo.ADMIN){
            app.goUserView();
            GlobalOption.parents.push("manager");
        }
        else {
            app.goUserList();
        }
    }

    @FXML
    public void onBoxListAction(ActionEvent actionEvent) throws IOException{
       BoxListController controller = app.goBoxList();
        controller.createCabinetList(0);
    }

    @FXML
    public void onLogListAction(ActionEvent actionEvent) throws IOException{
        FXMLLoader fxmlLoader;

        fxmlLoader = new FXMLLoader(getClass().getResource("LogList.fxml"));


        Parent root = fxmlLoader.load();
        LogListController controller = fxmlLoader.getController();
        controller.setApp(app);
        app.getRootStage().getScene().setRoot(root);
    }

    @FXML
    public void onSettingtAction(ActionEvent actionEvent) throws IOException{
        app.goSetting();
    }

    @FXML
    public void onCabinetListAction(ActionEvent actionEvent) throws IOException{
        app.goCabinetList();
    }
    @FXML
    public void onBackAction(ActionEvent event){
        app.goHome();
    }

    @FXML
    public void onOpeningLogAction(ActionEvent event){
        app.goOpeningLog();
    }

    @FXML
    public void onExitPlatform(){
        app.stopApplication();
    }
}
