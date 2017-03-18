package com.zhy.smail;

import com.zhy.smail.cabinet.entity.CabinetInfo;
import com.zhy.smail.cabinet.service.CabinetService;
import com.zhy.smail.common.json.JsonResult;
import com.zhy.smail.component.SimpleDialog;
import com.zhy.smail.component.music.Speaker;
import com.zhy.smail.config.GlobalOption;
import com.zhy.smail.config.LocalConfig;
import com.zhy.smail.restful.RestfulResult;
import com.zhy.smail.restful.RfFaultEvent;
import com.zhy.smail.restful.RfResultEvent;
import com.zhy.smail.setting.service.OptionService;
import com.zhy.smail.user.entity.UserInfo;
import com.zhy.smail.user.service.UserService;
import com.zhy.smail.user.view.LoginController;
import com.zhy.smail.user.view.SplashController;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class MainController implements Initializable {
    @FXML
    private Pane logoImagePane;
    @FXML
    private Label lblMessage;
    @FXML
    private Label lblAppTitle;
    @FXML
    private Label lblOffline;
    @FXML
    private ImageView logoImageView;
    private int perTotal = 0;
    private int totalNumber = 0;
    private String typedStr;
    private boolean startGetTyped;
    private Timer timer;

    public void initialize(URL location, ResourceBundle resources) {
        String imageUrl = LocalConfig.getInstance().getLogoImage();
        if (imageUrl == null || imageUrl.length() == 0) {
            return;
        } else {
            try {
                String urlStr = "file:/".concat(imageUrl);
                Image logo = new Image(urlStr);
                logoImageView.setImage(logo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        timer = null;
        OptionService.loadOptions(new RestfulResult() {
            @Override
            public void doResult(RfResultEvent event) {

            }

            @Override
            public void doFault(RfFaultEvent event) {

            }
        });
        lblMessage.setVisible(false);
        if (GlobalOption.mainTitle != null) {
            setAppTitle(GlobalOption.mainTitle.getCharValue());
        }
        startGetTyped = false;
        lblOffline.setVisible(false);
        timer = new Timer();
        if(GlobalOption.remainTime == null) return;

        perTotal = GlobalOption.remainTime.getIntValue();
        if (perTotal == 0) {
            return;
        }
        if (LocalConfig.getInstance().getVideoFile() == null &&
                LocalConfig.getInstance().getVideoFile().length() == 0) {
            return;
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                perTotal--;
                if (perTotal == 0) {
                    timer.cancel();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            SplashController controller = app.goSplash();
                            if (!controller.play()) {
                                app.goHome();
                            }
                            ;
                        }
                    });
                }
            }
        }, 0, 1000);
    }

    private void destoryTimer() {
        if (timer != null) {
            timer.cancel();
        }
        timer = null;
    }

    @FXML
    public void onScreenMax(ActionEvent action) {
        app.getRootStage().setMaximized(true);
    }

    @FXML
    protected void onExitAction(ActionEvent event) {

        Platform.exit();
    }

    private MainApp app;

    public void setApp(MainApp app) {
        this.app = app;

        app.offlineProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                lblOffline.setVisible(newValue);
            }
        });
        /*app.getRootStage().addEventHandler(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                System.out.println("1=" + event.getCharacter());
            }
        });*/
}

    public void setAppTitle(String title) {
        lblAppTitle.setText(title);
    }

    @FXML
    private void onLoginAction(ActionEvent event) throws IOException {
        app.goLogin(3);
        destoryTimer();
    }

    @FXML
    private void onDeliveryAction(ActionEvent event) throws IOException {
        app.goLogin(2);
        destoryTimer();
    }

    @FXML
    private void onManagerAction(ActionEvent event) {
        app.goLogin(1);
        destoryTimer();
    }

    @FXML
    private void onHelpAction(ActionEvent event) throws IOException {
        app.goHelp();
        destoryTimer();
    }

    @FXML
    private void onKeyPressed(KeyEvent event) {
        System.out.println("Pressed: " + event.getText()+": " + event.getCode());
        if (event.getCode() == KeyCode.SEMICOLON) {
            startGetTyped = true;
            typedStr = "";
        } else if (startGetTyped && event.getCode() == KeyCode.ENTER) {
            startGetTyped = false;
            startToLogin(typedStr);
        }
    }

    @FXML
    private void onKeyTyped(KeyEvent event){
        System.out.println("onKeyTyped " + event.getCharacter()+": " + event.getCode());
        if(event.getCharacter().equals(";") || event.getCharacter().equals("；")){
            startGetTyped = true;
            typedStr = "";
        }
        else if (startGetTyped) {
            typedStr += event.getCharacter();
        }
    }

    private void startToLogin(String cardNo) {
        UserService.getByCardNo(cardNo, new RestfulResult() {
            @Override
            public void doResult(RfResultEvent event) {
                if (event.getResult() == JsonResult.FAIL) {
                    Speaker.invalideCard();
                    SimpleDialog.showMessageDialog(app.getRootStage(), "无效卡", "");
                } else {
                    UserInfo user = (UserInfo) event.getData();
                    GlobalOption.currentUser = user;
                    LoginController loginController = null;
                    switch (user.getUserType()) {
                        case UserInfo.FACTORY_USER:
                        case UserInfo.ADMIN:
                        case UserInfo.ADVANCED_ADMIN:
                            loginController = app.goLogin(1);
                            break;
                        case UserInfo.DELIVERY:
                        case UserInfo.MAILMAN:
                            loginController = app.goLogin(2);
                            break;
                        case UserInfo.OWNER:
                            if (GlobalOption.cardNeedPassword.getIntValue() == 0) {
                                getCurrentCabient("本地箱柜号没有设置,请联系系统管理员。");
                                app.goOwner();
                                return;
                            } else {
                                loginController = app.goLogin(3);
                            }
                            break;
                    }
                    if (loginController != null) {
                        loginController.setUserName(user.getUserName());
                        loginController.passwordFocus();
                    }
                }
                destoryTimer();
            }

            @Override
            public void doFault(RfFaultEvent event) {

            }
        });
    }

    private void getCurrentCabient(String message) {
        String localCabinet = LocalConfig.getInstance().getLocalCabinet();
        if (localCabinet == null || localCabinet.length() == 0) {
            SimpleDialog.showMessageDialog(app.getRootStage(), message, "错误");
        } else {
            CabinetService.getByCabinetNo(localCabinet, new RestfulResult() {
                @Override
                public void doResult(RfResultEvent event) {
                    if (event.getResult() == RfResultEvent.OK) {
                        GlobalOption.currentCabinet = (CabinetInfo) event.getData();
                    } else {
                        SimpleDialog.showMessageDialog(app.getRootStage(), message, "错误");
                    }
                }

                @Override
                public void doFault(RfFaultEvent event) {

                }
            });
        }
    }
}
