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

    private MainApp app;
    private Integer userType;

    public void setApp(MainApp app) {
        this.app = app;
        app.createTimeout(lblTimer);
    }

    public void initialize(URL location, ResourceBundle resources){
        UserInfo user = GlobalOption.currentUser;
        userType = user.getUserType();
        if(user.getUserType() == UserInfo.FACTORY_USER) return;

        userListButton.setText("帐号信息");
        if(userType == UserInfo.ADMIN){
            settingButton.setVisible(false);
        }
    }


    @FXML
    public void onUserListAction(ActionEvent actionEvent) throws IOException{
        if(userType != UserInfo.FACTORY_USER){
            app.goUserView();
            GlobalOption.parents.push("manager");
        }
        else {
            app.goUserList();
        }
    }

    @FXML
    public void onBoxListAction(ActionEvent actionEvent) throws IOException{
       app.goBoxList();
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
        FXMLLoader fxmlLoader;

        fxmlLoader = new FXMLLoader(getClass().getResource("../../setting/view/Setting.fxml"));
        Parent root = fxmlLoader.load();
        SettingController controller = (SettingController)fxmlLoader.getController();
        app.getRootStage().getScene().setRoot(root);
        controller.setApp(app);
    }

    @FXML
    public void onCabinetListAction(ActionEvent actionEvent) throws IOException{
        FXMLLoader fxmlLoader;
        fxmlLoader = new FXMLLoader(getClass().getResource("../../cabinet/view/CabinetList.fxml"));
        Parent root = fxmlLoader.load();
        CabinetListController controller = (CabinetListController) fxmlLoader.getController();

        app.getRootStage().getScene().setRoot(root);
        controller.setApp(app);
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
