package com.zhy.smail.user.view;

/**
 * Created by wenliz on 2017/1/20.
 */

import com.zhy.smail.MainApp;
import com.zhy.smail.cabinet.entity.CabinetInfo;
import com.zhy.smail.cabinet.service.CabinetService;
import com.zhy.smail.common.utils.KeySecurity;
import com.zhy.smail.component.SimpleDialog;
import com.zhy.smail.component.TimeoutTimer;
import com.zhy.smail.component.keyboard.control.KeyboardPane;
import com.zhy.smail.component.music.Speaker;
import com.zhy.smail.config.GlobalOption;
import com.zhy.smail.config.LocalConfig;
import com.zhy.smail.lcp.LcProtocol;
import com.zhy.smail.lcp.command.LcCommand;
import com.zhy.smail.restful.DefaultRestfulResult;
import com.zhy.smail.restful.RestfulResult;
import com.zhy.smail.restful.RfFaultEvent;
import com.zhy.smail.restful.RfResultEvent;
import com.zhy.smail.serial.GatewayException;
import com.zhy.smail.serial.SerialGateway;
import com.zhy.smail.setting.service.OptionService;
import com.zhy.smail.task.SendManager;
import com.zhy.smail.user.entity.UserInfo;
import com.zhy.smail.user.service.UserService;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable{
    private Integer loginType;
    private MainApp app;
    private TimeoutTimer timer;

    @FXML
    private Button registerButton;
    @FXML
    private TextField txtUserName;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Label lblTimer;
    @FXML
    private ToolBar topToolbar;
    @FXML
    private GridPane loginGrid;
    @FXML
    private Button loginButton;

    KeyboardPane kb;

    public void setUserName(String userName){
        txtUserName.setText(userName);
        txtUserName.setDisable(true);
    }
    public void passwordFocus(){
        txtPassword.requestFocus();
    }

    public MainApp getApp() {
        return app;
    }

    public void setApp(MainApp app) {

        this.app = app;

        Stage stage = app.getRootStage();
        Scene scene = app.getRootScene();
        app.createTimeout(lblTimer,15);
        /*KeyBoardPopup popup = new KeyBoardPopup(kb);

        Bounds textNodeBounds = txtUserName.localToScreen(txtUserName.getBoundsInLocal());
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        popup.setX(textNodeBounds.getMinX());
        popup.setY( screenBounds.getMaxY()-popup.getHeight());




        popup.registerScene(scene);
        popup.setVisible(true);*/


        //popup.addDoubleClickEventFilter(stage);
        //popup.addFocusListener(scene);
        //popup.addGlobalFocusListener();

    }

    public void initialize(URL location, ResourceBundle resources){
        txtPassword.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(newValue){
                    Speaker.inputPassword();
                }
            }
        });

        txtUserName.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(newValue){
                    if(loginType == 1){
                        Speaker.inputManagerUser();
                    }
                    else if(loginType == 2){
                        Speaker.inputUserName();
                    }
                    else if(loginType == 3){
                        Speaker.inputRoom();
                    }
                }
            }
        });


        OptionService.loadOptions(new RestfulResult() {
            @Override
            public void doResult(RfResultEvent event) {

            }

            @Override
            public void doFault(RfFaultEvent event) {

            }
        });
    }

    public Integer getLoginType() {
        return loginType;
    }

    public void setLoginType(Integer loginType) {
        this.loginType = loginType;
    }

    @FXML
    public void onLoginAction(ActionEvent actionEvent) throws IOException {
        String userName = txtUserName.getText();
        String password = KeySecurity.encrypt(txtPassword.getText());
        if (userName.length() == 0) {
            SimpleDialog.showMessageDialog(app.getRootStage(), "请输入帐号，账号不能为空", "错误");
            txtUserName.requestFocus();
            return;
        }

        if (password.length() == 0) {
            SimpleDialog.showMessageDialog(app.getRootStage(), "请输入密码，密码不能为空", "错误");
            txtPassword.requestFocus();
            return;
        }

        UserService.login(userName, password, new RestfulResult() {
            @Override
            public void doResult(RfResultEvent event) {
                if (event.getResult() != 0) {
                    Speaker.userOrPasswordError();
                    SimpleDialog.showMessageDialog(app.getRootStage(), "帐号或密码错误，请重新输入", "错误");
                    txtUserName.requestFocus();
                    return;
                } else {
                    UserInfo user = (UserInfo) event.getData();
                    GlobalOption.currentUser = user;
                    if(GlobalOption.runMode == 1 && user.getUserType()>UserInfo.ADMIN){
                        SimpleDialog.showAutoCloseError(app.getRootStage(), "系统处于脱机状态，只有管理员才能登录。");
                        return;
                    }
                    if (user != null) {
                        loginForward(user);
                    }

                }
            }

            @Override
            public void doFault(RfFaultEvent event) {
                SimpleDialog.showMessageDialog(app.getRootStage(), "非法的帐号或密码，请重新输入", "错误");
                txtUserName.requestFocus();
            }
        });
    };


    private void loginForward(UserInfo user) {
        switch (user.getUserType()) {
            case UserInfo.FACTORY_USER:
            case UserInfo.ADMIN:
            case UserInfo.ADVANCED_ADMIN:
                goManager();
                break;
            case UserInfo.DELIVERY:
                goCommonDelivery();
                break;
            case UserInfo.MAILMAN:
                goDelivery();
                break;
            case UserInfo.OWNER:
                goOwner();
                break;
        }
    }
    private void goCommonDelivery(){
        String localCabinet = LocalConfig.getInstance().getLocalCabinet();
        if(localCabinet == null || localCabinet.length()==0){
            SimpleDialog.showMessageDialog(app.getRootStage(), "本地箱柜号没有设置,请联系系统管理员。","错误");
        }
        else{
            CabinetService.getByCabinetNo(localCabinet, new RestfulResult() {
                @Override
                public void doResult(RfResultEvent event) {
                    if(event.getResult() == RfResultEvent.OK){
                        GlobalOption.currentCabinet = (CabinetInfo)event.getData();
                        app.goCommonDelivery();
                    }
                    else{
                        SimpleDialog.showMessageDialog(app.getRootStage(), "本地箱柜号没有设置,请联系系统管理员。","错误");
                    }
                }

                @Override
                public void doFault(RfFaultEvent event) {

                }
            });
        }
    }

    private void goManager(){
        String localCabinet = LocalConfig.getInstance().getLocalCabinet();
        if(localCabinet == null || localCabinet.length()==0){
            SimpleDialog.showMessageDialog(app.getRootStage(), "本地箱柜号没有设置,请到【系统设置】中设置。","错误");
        }
        else{
            CabinetService.getByCabinetNo(localCabinet, new RestfulResult() {
                @Override
                public void doResult(RfResultEvent event) {
                    if(event.getResult() == RfResultEvent.OK){
                        GlobalOption.currentCabinet = (CabinetInfo)event.getData();
                    }
                    else{
                        SimpleDialog.showMessageDialog(app.getRootStage(), "错误的本地箱柜号，请到【系统设置】中重新设置。","错误");
                    }
                }

                @Override
                public void doFault(RfFaultEvent event) {

                }
            });
        }

        app.goManager();
    }

    private void goDelivery(){
        String localCabinet = LocalConfig.getInstance().getLocalCabinet();
        if(localCabinet == null || localCabinet.length()==0){
            SimpleDialog.showMessageDialog(app.getRootStage(), "本地箱柜号没有设置,请联系系统管理员。","错误");
        }
        else{
            CabinetService.getByCabinetNo(localCabinet, new RestfulResult() {
                @Override
                public void doResult(RfResultEvent event) {
                    if(event.getResult() == RfResultEvent.OK){
                        GlobalOption.currentCabinet = (CabinetInfo)event.getData();
                        app.goDelivery();
                    }
                    else{
                        SimpleDialog.showMessageDialog(app.getRootStage(), "本地箱柜号没有设置,请联系系统管理员。","错误");
                    }
                }

                @Override
                public void doFault(RfFaultEvent event) {

                }
            });
        }

    }

    private void goOwner(){
        String localCabinet = LocalConfig.getInstance().getLocalCabinet();
        if(localCabinet == null || localCabinet.length()==0){
            SimpleDialog.showMessageDialog(app.getRootStage(), "本地箱柜号没有设置,请联系系统管理员。","错误");
        }
        else{
            CabinetService.getByCabinetNo(localCabinet, new RestfulResult() {
                @Override
                public void doResult(RfResultEvent event) {
                    if(event.getResult() == RfResultEvent.OK){
                        GlobalOption.currentCabinet = (CabinetInfo)event.getData();
                        app.goOwner();
                    }
                    else{
                        SimpleDialog.showMessageDialog(app.getRootStage(), "本地箱柜号没有设置,请联系系统管理员。","错误");
                    }
                }

                @Override
                public void doFault(RfFaultEvent event) {

                }
            });
        }
    }



    @FXML
    public void onReadVersion(ActionEvent event) {
        LcProtocol protocol = new LcProtocol();
        byte[] packet = protocol.pack(LcCommand.READ_VERSION, 1, 0);


    }
    @FXML
    private void onCloseAction(ActionEvent event){

        SendManager.gateway.close();

    }

    @FXML
    protected  void onConnectAction(ActionEvent event){

        if(SendManager.gateway==null) {
            SendManager.gateway = new SerialGateway();
        }
        if(SendManager.gateway.isOpened()) {
            onCloseAction(null);
        }
        else {
            try {
                SerialGateway gateway = (SerialGateway)SendManager.gateway;
                gateway.connect(gateway.getPortName());
                //connectButton.setText("关闭端口");
               /* ParamValueList paramList = ParamManager.getCheckParams();
                SendingTask progressTask = new SendingTask(paramList, BusinessType.GET_MODULE_BASE, true);
                progressTask.valueProperty().addListener(new ChangeListener() {
                    @Override
                    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                        if(newValue == null) return;

                        Integer realValue = (Integer) newValue;
                        if(realValue == 0){
                            //connectButton.setText("关闭端口");


                        }
                        else if(realValue == -1){
                            SendManager.gateway.close();
                        }
                    }
                });
                SimpleDialog.modelDialog(progressTask,"正在检查设备有效性...", "检查设备有效性");*/


            } catch (GatewayException e) {
                SimpleDialog.showMessageDialog(app.getRootStage(), "打开端口出错:" + e.getMessage(), "打开失败");
                //connectButton.setSelected(false);
            }
        }
    }

    @FXML
    public void onBackAction(ActionEvent event){
        app.goHome();
    }

    @FXML
    public void onUserNameKeyPressed(KeyEvent event){
        if(event.getCode() == KeyCode.ENTER){
            String userName = txtUserName.getText();
            if(userName !=null && userName.length()>0  && (userName.substring(0,1).equals(";")||userName.substring(0,1).equals("；"))){
                userName = userName.substring(1);
                UserService.getByCardNo(userName, new RestfulResult() {
                    @Override
                    public void doResult(RfResultEvent event) {
                        if(event.getResult() == RfResultEvent.OK && event.getData()!=null){
                            UserInfo user = (UserInfo) event.getData();
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    txtUserName.setText(user.getUserName());
                                    txtUserName.setDisable(true);
                                }
                            });
                        }
                    }

                    @Override
                    public void doFault(RfFaultEvent event) {

                    }
                });

            }
            txtPassword.requestFocus();
        }
    }

    @FXML
    public void onUserNameKeyTyped(KeyEvent event){

    }





}