package com.zhy.smail;

import com.zhy.smail.cabinet.view.BoxListController;
import com.zhy.smail.cabinet.view.CabinetListController;
import com.zhy.smail.common.controller.RootController;
import com.zhy.smail.common.utils.SystemUtil;
import com.zhy.smail.component.SimpleDialog;
import com.zhy.smail.component.TimeoutTimer;
import com.zhy.smail.component.keyboard.control.KeyBoardPopup;
import com.zhy.smail.component.keyboard.control.KeyBoardPopupBuilder;
import com.zhy.smail.component.music.Speaker;
import com.zhy.smail.config.GlobalOption;
import com.zhy.smail.config.LocalConfig;
import com.zhy.smail.delivery.view.*;
import com.zhy.smail.manager.view.LogListController;
import com.zhy.smail.manager.view.ManagerController;
import com.zhy.smail.manager.view.OpeningLogController;
import com.zhy.smail.pickup.view.PickupController;
import com.zhy.smail.restful.RestfulResult;
import com.zhy.smail.restful.RfFaultEvent;
import com.zhy.smail.restful.RfResultEvent;
import com.zhy.smail.serial.GatewayException;
import com.zhy.smail.serial.SerialGateway;
import com.zhy.smail.serial.UdpGateway;
import com.zhy.smail.setting.entity.SystemOption;
import com.zhy.smail.setting.service.OptionService;
import com.zhy.smail.task.ResponseManager;
import com.zhy.smail.task.SendManager;
import com.zhy.smail.task.SendingTask;
import com.zhy.smail.user.service.UserService;
import com.zhy.smail.user.view.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Locale;
import java.util.logging.Logger;

public class MainApp extends Application {
    private static Log logger = LogFactory.getLog(MainApp.class);

    private Scene rootScene;
    private Stage rootStage;
    private MainController mainController;
    private TimeoutTimer timer = null;
    private Thread responseThread;
    private ResponseManager responseManager;
    private SimpleBooleanProperty offline;

    public boolean isOffline() {
        return offline.get();
    }

    public SimpleBooleanProperty offlineProperty() {
        return offline;
    }

    public void setOffline(boolean offline) {
       this.offline.set(offline);
    }

    public TimeoutTimer getTimer() {
        return timer;
    }

    public void setTimer(TimeoutTimer timer) {
        this.timer = timer;
    }

    public Scene getRootScene() {
        return rootScene;
    }

    public Stage getRootStage() {
        return rootStage;
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        this.offline = new SimpleBooleanProperty(false);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main.fxml"));
        Parent root = fxmlLoader.load();
        mainController = fxmlLoader.getController();

        primaryStage.setTitle("ZY Mail");
        rootScene = new Scene(root, 1280, 1024);
        rootScene.getStylesheets().add("style.css");
        primaryStage.setScene(rootScene);
        rootStage = primaryStage;
        mainController.setApp(this);

        //primaryStage.setMaximized(true);

        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.show();
        rootScene.getWindow().centerOnScreen();
        initVK(rootStage);
        openCom();
        //initUDP();
        testConnection();
        Speaker.welcome();
        rootStage.addEventFilter(KeyEvent.KEY_PRESSED, keyEventEventHandler);
        rootStage.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEventEventHandler);
        rootStage.addEventFilter(MouseEvent.MOUSE_MOVED, mouseEventEventHandler);
        checkRegisterNo();
    }

    private void checkRegisterNo() {
        String registerNo = SystemUtil.getRegisterNo();
        if (registerNo.equals(LocalConfig.getInstance().getRegisterNo())) {
            return;
        }
        goRegister();
    }

    private void initVK(Stage primaryStage) {
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        double height = rootScene.getWindow().getHeight();
        double width = rootScene.getWindow().getWidth();
        double scale = Math.min(height / 550, width / 650);
        KeyBoardPopupBuilder builder = KeyBoardPopupBuilder.create();
        builder.initScale(scale);
        builder.initLocale(Locale.CHINESE);
        KeyBoardPopup popup = builder.build();
        popup.addDoubleClickEventFilter(primaryStage);
        popup.addFocusListener(rootScene);
        popup.addGlobalFocusListener();
    }

    public void testConnection() {
        Task<Integer> testTask = new Task<Integer>() {
            private Integer resultValue;
            private boolean checked = false;

            @Override
            protected Integer call() throws Exception {
                updateMessage("正在连接服务器...");
                checked = false;
                if(GlobalOption.appMode == 0){
                   trySleep(5000);
                }
                updateMessage("正在连接服务器1...");
                test();

                return resultValue;
            }

            private void trySleep(long m){
                try {
                    Thread.sleep(m);
                }
                catch (Exception e){

                }
            }

            private void test(){

                UserService.testConnection(new RestfulResult() {
                    @Override
                    public void doResult(RfResultEvent event) {
                        updateValue(0);
                        GlobalOption.runMode = 0;
                        setOffline(false);

                        loadSetting();
                        resultValue = 0;
                    }

                    @Override
                    public void doFault(RfFaultEvent event) {
                        if(GlobalOption.appMode == 0 && !checked){
                            updateMessage("正在连接服务器2...");
                            trySleep(30000);
                            updateMessage("正在连接服务器3...");
                            checked = true;
                            test();
                        }
                        else {
                            resultValue = -1;
                            updateValue(-1);
                            if (event.getErrorNo() == -1) {
                                updateMessage(event.getMessage());
                            }

                            updateMessage("连接服务器(" + GlobalOption.serverIP + ")失败.本机进入脱机状态，只有管理员才能登录.");
                            GlobalOption.serverIP = "127.0.0.1";
                            GlobalOption.runMode = 1;
                            setOffline(true);
                        }
                    }
                });
            }
        };
        SimpleDialog.showDialog(rootStage, testTask, "正在连接到服务器...", "连接");
    }

    public void loadSetting() {
        OptionService.getById(SystemOption.APP_TITLE_ID, new RestfulResult() {
            @Override
            public void doResult(RfResultEvent event) {
                SystemOption option = (SystemOption) event.getData();
                if (option != null) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            mainController.setAppTitle(option.getCharValue());
                        }
                    });
                }
            }

            @Override
            public void doFault(RfFaultEvent event) {

            }
        });
    }

    public void setAppTitle(String title) {
        mainController.setAppTitle(title);
    }

    private void initUDP() {
        if (SendManager.gateway == null) {
            SendManager.gateway = new UdpGateway(8010);
        }
        if (SendManager.gateway.isOpened()) {
            SendManager.gateway.close();
        } else {
            UdpGateway gateway = (UdpGateway) SendManager.gateway;
            try {
                gateway.startGateway();
                responseManager = new ResponseManager();
                responseThread = new Thread(responseManager);
                responseThread.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void closeCom() {
        if (SendManager.gateway == null) return;

        if (SendManager.gateway.isOpened()) {
            SendManager.gateway.close();
        }

        if (responseManager != null) {
            responseManager.setCanceled(true);
            try {
                responseThread.join();
            } catch (InterruptedException e) {

            }
        }
    }

    public void openCom() {

        if (SendManager.gateway == null) {
            SendManager.gateway = new SerialGateway();
        }
        if (SendManager.gateway.isOpened()) {
            SendManager.gateway.close();
        } else {
            SerialGateway gateway = (SerialGateway) SendManager.gateway;
            String portName = gateway.getPortName();
            try {
                gateway.connect(gateway.getPortName());

                responseManager = new ResponseManager();
                responseThread = new Thread(responseManager);
                responseThread.start();


            } catch (GatewayException e) {
                SimpleDialog.showMessageDialog(getRootStage(), "打开端口" + portName + "出错:" + e.getMessage(), "打开失败");
            }
        }
    }


    public static void main(String[] args) {
        LocalConfig local = LocalConfig.getInstance();
        GlobalOption.appMode = local.getAppMode();
        if (local.getAppMode() == LocalConfig.APP_MASTER) {
            GlobalOption.serverIP = "127.0.0.1";
        } else {
            GlobalOption.serverIP = local.getServerIP();
        }
        launch(args);

    }

    public void goHome() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main.fxml"));
            Parent root = fxmlLoader.load();
            mainController = fxmlLoader.getController();
            mainController.setApp(this);
            rootScene.setRoot(root);
        } catch (Exception e) {
            SimpleDialog.showMessageDialog(getRootStage(), e.getMessage(), "错误");
        }

    }

    public LoginController goLogin(Integer loginType) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("user/view/login.fxml"));
            Parent root = fxmlLoader.load();
            LoginController controller = fxmlLoader.getController();
            getRootScene().setRoot(root);
            controller.setLoginType(loginType);
            controller.setApp(this);
            return controller;
        } catch (Exception e) {
            SimpleDialog.showMessageDialog(getRootStage(), e.getMessage(), "错误");
        }
        return null;
    }

    public void goManager() {
        try {
            FXMLLoader fxmlLoader;
            fxmlLoader = new FXMLLoader(getClass().getResource("manager/view/manager.fxml"));
            Parent root = fxmlLoader.load();
            ManagerController controller = fxmlLoader.getController();
            getRootStage().getScene().setRoot(root);
            controller.setApp(this);

        } catch (Exception e) {
            SimpleDialog.showMessageDialog(getRootStage(), e.getMessage(), "错误");
        }
    }

    public void goOwner() {
        try {
            FXMLLoader fxmlLoader;
            fxmlLoader = new FXMLLoader(getClass().getResource("pickup/view/pickup.fxml"));
            Parent root = fxmlLoader.load();
            getRootStage().getScene().setRoot(root);
            PickupController controller = fxmlLoader.getController();
            controller.setApp(this);

        } catch (Exception e) {
            SimpleDialog.showMessageDialog(this.getRootStage(), e.getMessage(), "错误");
        }
    }

    public void goDelivery() {
       loadFxml("delivery/view/delivery.fxml");
    }

    public ChangePasswordController goChangePassword() {
        try {
            FXMLLoader fxmlLoader;
            fxmlLoader = new FXMLLoader(getClass().getResource("user/view/ChangePassword.fxml"));
            Parent root = fxmlLoader.load();
            getRootStage().getScene().setRoot(root);
            ChangePasswordController controller = fxmlLoader.getController();
            controller.setApp(this);
            return controller;
        } catch (Exception e) {
            SimpleDialog.showMessageDialog(this.getRootStage(), e.getMessage(), "错误");
            return null;
        }
    }

    public void goCabinetList() {
        try {
            FXMLLoader fxmlLoader;
            fxmlLoader = new FXMLLoader(getClass().getResource("cabinet/view/CabinetList.fxml"));
            Parent root = fxmlLoader.load();
            CabinetListController controller = (CabinetListController) fxmlLoader.getController();
            getRootStage().getScene().setRoot(root);
            controller.setApp(this);
        } catch (Exception e) {
            SimpleDialog.showMessageDialog(getRootStage(), e.getMessage(), "错误");
        }
    }

    public BoxListController goBoxList() {
        return (BoxListController) loadFxml("cabinet/view/BoxList.fxml");
    }

    public UserListController goUserList() {
        try {
            FXMLLoader fxmlLoader;
            fxmlLoader = new FXMLLoader(getClass().getResource("user/view/UserList.fxml"));
            Parent root = fxmlLoader.load();
            UserListController controller = (UserListController) fxmlLoader.getController();
            controller.setApp(this);
            getRootStage().getScene().setRoot(root);
            return controller;
        } catch (Exception e) {
            SimpleDialog.showMessageDialog(getRootStage(), e.getMessage(), "错误");
            return null;
        }
    }

    public void goHelp() {
        loadFxml("user/view/Help.fxml");
    }

    public PutdownController goPutdown() {
        return (PutdownController) loadFxml("delivery/view/putdown.fxml");
    }

    public PutmailController goPutmail() {
        return (PutmailController) loadFxml("delivery/view/putmail.fxml");
    }

    public UserChoiceController goUserChoice() {
        return (UserChoiceController) loadFxml("user/view/userChoice.fxml");
    }

    public ConfirmDeliveryController goConfirmDelivery() {
        return (ConfirmDeliveryController) loadFxml("delivery/view/confirmDelivery.fxml");
    }

    public UserViewController goUserView() {
        return (UserViewController) loadFxml("user/view/UserView.fxml");
    }

    public OccupyBoxController goOccupyBox() {
        return (OccupyBoxController) loadFxml("delivery/view/occupyBox.fxml");
    }

    public OpeningLogController goOpeningLog() {
        return (OpeningLogController) loadFxml("manager/view/openingLog.fxml");
    }

    public SplashController goSplash() {
        return (SplashController) loadFxml("user/view/splash.fxml");
    }

    public void goRegister() {
        loadFxml("user/view/register.fxml");
    }

    public void goSetting() {
        loadFxml("setting/view/Setting.fxml");
    }

    public void goQueryRecord() {
        loadFxml("manager/view/queryRecord.fxml");
    }

    public CommonDeliveryController goCommonDelivery() {
        return (CommonDeliveryController) loadFxml("delivery/view/commonDelivery.fxml");
    }

    public SelectRoomController goSelectRoom() {
        return (SelectRoomController) loadFxml("delivery/view/selectRoom.fxml");
    }

    public void goPickupLog() {
        loadFxml("pickup/view/pickuplog.fxml");
    }

    public LogListController goLogList() {
        return (LogListController) loadFxml("manager/view/LogList.fxml");
    }

    private RootController loadFxml(String path) {
        try {
            FXMLLoader fxmlLoader;
            fxmlLoader = new FXMLLoader(getClass().getResource(path));
            Parent root = fxmlLoader.load();
            RootController controller = (RootController) fxmlLoader.getController();
            controller.setApp(this);
            getRootStage().getScene().setRoot(root);
            return controller;
        } catch (Exception e) {
            SimpleDialog.showMessageDialog(getRootStage(), e.getMessage(), "错误");
            return null;
        }
    }

    EventHandler<KeyEvent> keyEventEventHandler = new EventHandler<KeyEvent>() {
        public void handle(final KeyEvent keyEvent) {
            if (timer != null) {
                timer.restart();
            }
            Speaker.keyTypeSound();
        }
    };

    EventHandler<MouseEvent> mouseEventEventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            if (timer != null) {
                timer.restart();
            }
        }
    };

    public void createTimeout(Label lblTimer) {
        createTimeout(lblTimer, GlobalOption.TimeoutTotal);
    }

    public void createTimeout(Label lblTimer, Integer timeout) {
        if (timer != null) {
            timer.cancel();
        }
        timer = new TimeoutTimer(lblTimer, timeout, new TimeoutTimer.TimeoutCallback() {
            @Override
            public void run() {
                goHome();
            }
        });
        timer.start();
    }

    public void stopApplication() {
        Platform.exit();
    }

    public void stop() throws Exception {
        if (timer != null) {
            timer.cancel();

            timer = null;
        }
        if (responseManager != null) {
            responseManager.setCanceled(true);
            try {
                responseThread.join();
            } catch (InterruptedException e) {

            }
        }
        if (SendManager.gateway != null) {
            if (SendManager.gateway.isOpened()) {
                SendManager.gateway.close();
            }
            SendManager.gateway = null;
        }
    }
}
