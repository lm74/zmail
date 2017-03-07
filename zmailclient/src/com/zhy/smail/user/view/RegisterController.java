package com.zhy.smail.user.view;

import com.zhy.smail.common.controller.RootController;
import com.zhy.smail.common.utils.SystemUtil;
import com.zhy.smail.component.SimpleDialog;
import com.zhy.smail.config.LocalConfig;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by wenliz on 2017/3/6.
 */
public class RegisterController  extends RootController implements Initializable {
    @FXML
    private TextField txtSerialNo;
    @FXML
    private TextField txtRegisterNo;

    public void initialize(URL location, ResourceBundle resources){
        txtSerialNo.setText(SystemUtil.getSerialNo());
    }

    @FXML
    private void  onRegisterAction(ActionEvent event){
        if(SystemUtil.getRegisterNo().equals(txtRegisterNo.getText())){
            SimpleDialog.showMessageDialog(app.getRootStage(), "注册成功!","注册成功");
            LocalConfig.getInstance().setRegisterNo(txtRegisterNo.getText());
            LocalConfig.getInstance().saveProperties();
            app.goHome();
        }
        else{
            SimpleDialog.showMessageDialog(app.getRootStage(), "错误的注册码，请输入正确的注册码。","注册成功");
        }
    }

    @FXML
    private void onExitPlatformAction(ActionEvent event){
        Platform.exit();
    }
}
