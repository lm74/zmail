package com.zhy.smail.setting.view;

import com.zhy.smail.MainApp;
import com.zhy.smail.common.controller.RootController;
import com.zhy.smail.component.SimpleDialog;
import com.zhy.smail.component.keyboard.control.VkProperties;
import com.zhy.smail.config.GlobalOption;
import com.zhy.smail.config.LocalConfig;
import com.zhy.smail.restful.RestfulResult;
import com.zhy.smail.restful.RfFaultEvent;
import com.zhy.smail.restful.RfResultEvent;
import com.zhy.smail.serial.SerialGateway;
import com.zhy.smail.serial.SerialPortInfo;
import com.zhy.smail.setting.entity.SystemOption;
import com.zhy.smail.setting.service.OptionService;
import com.zhy.smail.task.SendManager;
import com.zhy.smail.user.entity.UserInfo;
import com.zhy.smail.user.service.UserService;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by wenliz on 2017/2/15.
 */
public class SettingController extends RootController implements Initializable {

    @FXML
    private Label lblTimer;
    @FXML
    private ToolBar topToolBar;
    @FXML
    private Region topLeft;
    @FXML
    private Region topRight;
    @FXML
    private RadioButton rdoMaster;
    @FXML
    private RadioButton rdoSlave;
    @FXML
    private TextField txtServerIP;
    @FXML
    private TextField txtTimeout;
    @FXML
    private TextField txtLocalCabinetNo;
    @FXML
    private ChoiceBox<String> protocolList;
    @FXML
    private TextField txtDoorIP;
    @FXML
    private TextField txtDoorPort;
    @FXML
    private CheckBox chkNeedPasswordForCard;
    @FXML
    private  CheckBox chkDeliverSameMail;
    @FXML
    private ChoiceBox<String> comPortList;
    @FXML
    private TextField txtMainTitle;
    @FXML
    private TextField txtBuildingNo;
    @FXML
    private TextField txtUnitNo;
    @FXML
    private Label lblFMessage;
    @FXML
    private TextField txtUseDays;
    @FXML
    private Tab factoryTab;
    @FXML
    private TabPane userTabPane;
    @FXML
    private Label lblDMessage;
    @FXML
    private Button testButton;
    @FXML
    private Label lblOffline;
    @FXML
    private TextField txtVideoFile;
    @FXML
    private TextField txtRemainTime;

    private int currentProtocolIndex;


    public void setApp(MainApp app) {
        this.app = app;
        app.createTimeout(lblTimer);
        app.offlineProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                lblOffline.setVisible(newValue);
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        HBox.setHgrow(topLeft, Priority.ALWAYS);
        HBox.setHgrow(topRight, Priority.ALWAYS);
        lblOffline.setVisible(false);
        txtLocalCabinetNo.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_NUMERIC);
        txtServerIP.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_NUMERIC);
        txtTimeout.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_NUMERIC);
        txtDoorIP.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_NUMERIC);
        txtDoorPort.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_NUMERIC);
        txtBuildingNo.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_NUMERIC);
        txtUnitNo.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_NUMERIC);
        txtRemainTime.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_NUMERIC);
        if(GlobalOption.currentUser.getUserType()!= UserInfo.FACTORY_USER){
            factoryTab.setDisable(true);
            //userTabPane.getTabs().remove(factoryTab);
        }

        protocolList.getItems().addAll("自有协议(UDP)","自有协议(TCP)");

        LocalConfig config = LocalConfig.getInstance();
        txtLocalCabinetNo.setText(config.getLocalCabinet());
        txtServerIP.setText(config.getServerIP());
        if(config.getAppMode() == 0){
            rdoMaster.setSelected(true);
            txtServerIP.setDisable(true);
        }
        else{
            rdoSlave.setSelected(true);
            txtServerIP.setDisable(false);
        }
        txtVideoFile.setText(config.getVideoFile());
        comPortList.getItems().addAll(SerialPortInfo.getPortList());
        comPortList.getSelectionModel().select(config.getSerialPortInfo().getPortName());
        OptionService.loadOptions(new RestfulResult() {
            @Override
            public void doResult(RfResultEvent event) {
                if(event.getResult() == RfResultEvent.OK){
                    txtTimeout.setText(GlobalOption.timeout.getIntValue().toString());
                    txtBuildingNo.setText(GlobalOption.buildingNo.getIntValue().toString());
                    txtUnitNo.setText(GlobalOption.unitNo.getIntValue().toString());
                    txtDoorIP.setText(GlobalOption.doorServerIp.getCharValue());
                    txtDoorPort.setText(GlobalOption.doorServerPort.getIntValue().toString());
                    if(GlobalOption.cardNeedPassword.getIntValue() == 1){
                        chkNeedPasswordForCard.setSelected(true);
                    }
                    else{
                        chkNeedPasswordForCard.setSelected(false);
                    }
                    if(GlobalOption.deliverySameMail.getIntValue() == 1){
                        chkDeliverSameMail.setSelected(true);
                    }
                    else{
                        chkDeliverSameMail.setSelected(false);
                    }
                    txtMainTitle.setText(GlobalOption.mainTitle.getCharValue());
                    txtUseDays.setText(GlobalOption.useDays.getIntValue().toString());
                    if(GlobalOption.remainTime == null || GlobalOption.remainTime.getIntValue() == null){
                        txtRemainTime.setText("0");
                    }
                    else {
                        txtRemainTime.setText(GlobalOption.remainTime.getIntValue().toString());
                    }

                    currentProtocolIndex = GlobalOption.doorProtocol.getIntValue();

                    protocolList.getSelectionModel().select(currentProtocolIndex);
                    protocolList.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
                        @Override
                        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                            currentProtocolIndex = newValue.intValue();

                        }
                    });
                }
            }

            @Override
            public void doFault(RfFaultEvent event) {

            }
        });

    }

    @FXML
    private void onBackAction(ActionEvent event){
        app.goManager();
    }

    private void saveFactory(){
        LocalConfig config = LocalConfig.getInstance();
        int useDays = getInteger(txtUseDays);
        if(useDays != GlobalOption.useDays.getIntValue()){
            GlobalOption.useDays.setIntValue(useDays);
            saveOption(GlobalOption.useDays);
        }
        String newPortName = comPortList.getSelectionModel().getSelectedItem();
        if(!config.getSerialPortInfo().getPortName().equals(newPortName)){
            app.closeCom();
            config.getSerialPortInfo().setPortName(newPortName);
            if(SendManager.gateway instanceof SerialGateway){
                SerialGateway gateway = (SerialGateway) SendManager.gateway;
                gateway.setPortName(newPortName);
            }
            app.openCom();
        }
        String cabinetNo = txtLocalCabinetNo.getText();
        if(cabinetNo != null && cabinetNo.length()>0){
            config.setLocalCabinet(cabinetNo);
        }

        GlobalOption.mainTitle.setCharValue(txtMainTitle.getText());
        app.setAppTitle(GlobalOption.mainTitle.getCharValue());
        saveOption(GlobalOption.mainTitle);

        config.saveProperties();
        SimpleDialog.showAutoCloseInfo(app.getRootStage(), "保存成功");
    }

    @FXML
    private void onFactoryAction(ActionEvent event){
        LocalConfig config = LocalConfig.getInstance();

        int appMode = rdoMaster.isSelected()?0:1;
        if(appMode != config.getAppMode()){
            config.setAppMode(appMode);
            GlobalOption.appMode = appMode;
        }

        String ip = txtServerIP.getText();
        if(!ip.equals(config.getServerIP())) {
            config.setServerIP(ip);
            GlobalOption.serverIP = ip;
            testConnection(ip);
        }
        else{
            saveFactory();
        }




    }

    @FXML
    private void onMasterAction(ActionEvent event){
        txtServerIP.setText("127.0.0.1");
        txtServerIP.setDisable(true);
    }
    @FXML
    private void onSlaveAction(ActionEvent event){
        txtServerIP.setDisable(false);
    }

    private void testConnection(String ip){
        Task<Integer> testTask = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {
                updateMessage("正在连接服务器" + ip +"...");
                UserService.testConnection(new RestfulResult() {
                    @Override
                    public void doResult(RfResultEvent event) {
                        GlobalOption.runMode = 0;
                        updateValue(0);
                        updateMessage("连接服务器"+ip+"成功，系统恢复到正常工作状态。");
                        app.setOffline(false);
                        saveFactory();
                        app.loadSetting();
                    }

                    @Override
                    public void doFault(RfFaultEvent event) {
                        if(event.getErrorNo() == -1){
                            updateMessage(event.getMessage());
                        }
                        else {
                            updateMessage("连接服务器(" + GlobalOption.serverIP + ")失败, 保存失败！");
                            GlobalOption.serverIP = "127.0.0.1";
                            GlobalOption.runMode = 1;
                            app.setOffline(true);
                        }
                        updateValue(-1);
                    }
                });
                return -1;

            }
        };
        SimpleDialog.showDialog(app.getRootStage(), testTask,"正在连接到服务器...", "连接");
    }

    @FXML
    private void onSelectFileAction(ActionEvent event){
        FileChooser openFileChoose = new FileChooser();
        openFileChoose.setTitle("选择文件(mp4)");
        openFileChoose.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("MP4文件","*.mp4"));
        File file = openFileChoose.showOpenDialog(app.getRootStage());
        if(file != null){
            txtVideoFile.setText(file.getAbsolutePath());
        }

    }

    @FXML
    private void onGeneralSaveAction(ActionEvent event){
        saveGeneral();
    }

    private void saveGeneral(){
        LocalConfig config = LocalConfig.getInstance();


        config.setVideoFile(txtVideoFile.getText());
        config.saveProperties();

        Integer timeout = getInteger(txtTimeout);
        if(timeout!=GlobalOption.timeout.getIntValue()) {
            GlobalOption.timeout.setIntValue(timeout);
            saveOption(GlobalOption.timeout);
        }
        Integer buildingNo = getInteger(txtBuildingNo);
        if(buildingNo!=GlobalOption.buildingNo.getIntValue()) {
            GlobalOption.buildingNo.setIntValue(buildingNo);
            saveOption(GlobalOption.buildingNo);
        }
        Integer unitNo = getInteger(txtUnitNo);
        if(unitNo != GlobalOption.unitNo.getIntValue()) {
            GlobalOption.unitNo.setIntValue(unitNo);
            saveOption(GlobalOption.unitNo);
        }
        Integer remainTime = getInteger(txtRemainTime);
        if(remainTime != GlobalOption.remainTime.getIntValue()){
            GlobalOption.remainTime.setIntValue(remainTime);
            saveOption(GlobalOption.remainTime);
        }


        if(chkNeedPasswordForCard.isSelected()){
            GlobalOption.cardNeedPassword.setIntValue(1);
        }
        else{
            GlobalOption.cardNeedPassword.setIntValue(0);
        }
        saveOption(GlobalOption.cardNeedPassword);
        if(chkDeliverSameMail.isSelected()){
            GlobalOption.deliverySameMail.setIntValue(1);
        }
        else{
            GlobalOption.deliverySameMail.setIntValue(0);
        }
        saveOption(GlobalOption.deliverySameMail);

        SimpleDialog.showAutoCloseInfo(app.getRootStage(), "保存成功。");
    }

    private void saveOption(SystemOption option){
        OptionService.save(option, new RestfulResult() {
            @Override
            public void doResult(RfResultEvent event) {

            }

            @Override
            public void doFault(RfFaultEvent event) {
            }
        });
    }

    private Integer getInteger(TextField txt){
        Integer result = 0;
        try{
            result = Integer.valueOf(txt.getText());
        }
        catch (Exception e){

        }
        return  result;
    }

    @FXML
    private void onDoorSaveAction(ActionEvent event){
        if(!txtDoorIP.getText().equals(GlobalOption.doorServerIp.getCharValue())) {
            GlobalOption.doorServerIp.setCharValue(txtDoorIP.getText());
            saveOption(GlobalOption.doorServerIp);
        }
        int doorPort = getInteger(txtDoorPort);
        if(doorPort != GlobalOption.doorServerPort.getIntValue()) {
            GlobalOption.doorServerPort.setIntValue(getInteger(txtDoorPort));
            saveOption(GlobalOption.doorServerPort);
        }
        if(currentProtocolIndex != GlobalOption.doorProtocol.getIntValue()){
            GlobalOption.doorProtocol.setIntValue(currentProtocolIndex);
            saveOption(GlobalOption.doorProtocol);
        }
        SimpleDialog.showAutoCloseInfo(app.getRootStage(), "保存成功");
    }

    @FXML
    private void  onCabinetListAction(ActionEvent event){
        app.goCabinetList();
    }

    @FXML
    private void onTestAction(ActionEvent event){
        Integer protocolType = currentProtocolIndex;
        String serverIp = txtDoorIP.getText();
        if(serverIp == null && serverIp.length()==0){
            SimpleDialog.showMessageDialog(app.getRootStage(), "门禁IP地址不能为空","");
            txtServerIP.requestFocus();
            return;
        }
        Integer serverPort = getInteger(txtDoorPort);
        if(serverPort<=1024){
            SimpleDialog.showMessageDialog(app.getRootStage(), "门禁端口号必须大于1024，请重新输入！","");
            txtDoorPort.requestFocus();
            return;
        }
        testButton.setDisable(true);
        lblDMessage.setText("");

        Task<Integer> testTask = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {
                updateMessage("正在测试...");
                OptionService.testDoorSystem(protocolType, serverIp, serverPort, new RestfulResult() {
                    @Override
                    public void doResult(RfResultEvent event) {
                        String  message = "";
                        if(event.getResult() == RfResultEvent.OK){
                            message="测试成功.";
                        }
                        else if(event.getResult() == 1){
                            message="发包失败，请检查IP和端口是否正确.";
                        }
                        else if(event.getResult() == 2){
                            message="连接门禁系统失败，请检查IP和端口是否正确。";
                        }
                        else if(event.getResult() == 3){
                            message="登录门禁系统失败，请检查门禁系统是否运行正常。";
                        }
                        else{
                            message="未知错误，请检查IP和端口是否正确。";
                        }
                        updateMessage(message);
                        final String  showMesage = message;
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                lblDMessage.setText(showMesage);
                                testButton.setDisable(false);
                            }
                        });

                    }

                    @Override
                    public void doFault(RfFaultEvent event) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                testButton.setDisable(false);
                            }
                        });
                    }
                });
                return 0;
            }
        };

        SimpleDialog.showDialog(app.getRootStage(), testTask, "", "");

    }
}
