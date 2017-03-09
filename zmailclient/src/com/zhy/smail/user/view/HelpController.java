package com.zhy.smail.user.view;

import com.zhy.smail.MainApp;
import com.zhy.smail.common.controller.RootController;
import com.zhy.smail.config.GlobalOption;
import com.zhy.smail.user.entity.UserInfo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.web.WebView;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by wenliz on 2017/1/17.
 */
public class HelpController  extends RootController implements Initializable {
    @FXML
    private Label lblTimer;
    @FXML
    private ToolBar topToolBar;
    @FXML
    private Region topLeft;
    @FXML
    private Region topRight;
    @FXML
    private WebView helpView;

    public void setApp(MainApp app) {
        this.app = app;
        app.createTimeout(lblTimer);
    }


    public void initialize(URL location, ResourceBundle resources){
        HBox.setHgrow(topLeft, Priority.ALWAYS);
        HBox.setHgrow(topRight, Priority.ALWAYS);

//        helpView.getEngine().load(GlobalOption.getServerUrl() + "/help/index.html");
//        helpView.getEngine().load(GlobalOption.getServerRoot() + "/help/index.html");
    }

    @FXML
    private void onBackAction(ActionEvent event){
        app.goHome();
    }
}
