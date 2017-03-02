package com.zhy.smail.setting.view;

import com.zhy.smail.MainApp;
import com.zhy.smail.component.keyboard.control.VkProperties;
import com.zhy.smail.config.GlobalOption;
import com.zhy.smail.config.LocalConfig;
import com.zhy.smail.restful.RestfulResult;
import com.zhy.smail.restful.RfFaultEvent;
import com.zhy.smail.restful.RfResultEvent;
import com.zhy.smail.serial.SerialPortInfo;
import com.zhy.smail.setting.entity.SystemOption;
import com.zhy.smail.setting.service.OptionService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by wenliz on 2017/2/15.
 */
public class SettingController  implements Initializable {
    private MainApp app;

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


    public MainApp getApp() {
        return app;
    }

    public void setApp(MainApp app) {
        this.app = app;
        app.createTimeout(lblTimer);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        HBox.setHgrow(topLeft, Priority.ALWAYS);
        HBox.setHgrow(topRight, Priority.ALWAYS);
        txtLocalCabinetNo.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_NUMERIC);
        txtServerIP.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_NUMERIC);
        txtTimeout.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_NUMERIC);
        txtDoorIP.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_NUMERIC);
        txtDoorPort.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_NUMERIC);

        LocalConfig config = LocalConfig.getInstance();
        txtLocalCabinetNo.setText(config.getLocalCabinet());
        txtServerIP.setText(config.getServerIP());
        if(config.getAppMode() == 0){
            rdoMaster.setSelected(true);
        }
        else{
            rdoSlave.setSelected(true);
        }
        comPortList.getItems().addAll(SerialPortInfo.getPortList());
        comPortList.getSelectionModel().select(config.getSerialPortInfo().getPortName());
        OptionService.loadOptions(new RestfulResult() {
            @Override
            public void doResult(RfResultEvent event) {
                if(event.getResult() == RfResultEvent.OK){
                    txtTimeout.setText(GlobalOption.timeout.getIntValue().toString());
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

    @FXML
    private void onGeneralSaveAction(ActionEvent event){
        LocalConfig config = LocalConfig.getInstance();
        String cabinetNo = txtLocalCabinetNo.getText();
        if(cabinetNo != null && cabinetNo.length()>0){
            config.setLocalCabinet(cabinetNo);
        }
        String ip = txtServerIP.getText();
        config.setServerIP(ip);

        if(rdoMaster.isSelected()){
            config.setAppMode(0);
        }
        else{
            config.setAppMode(1);
        }
        config.getSerialPortInfo().setPortName(comPortList.getSelectionModel().getSelectedItem());
        config.saveProperties();

        Integer timeout = getInteger(txtTimeout);
        GlobalOption.timeout.setIntValue(timeout);
        saveOption(GlobalOption.timeout);

        GlobalOption.mainTitle.setCharValue(txtMainTitle.getText());
        saveOption(GlobalOption.mainTitle);
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
        GlobalOption.doorServerIp.setCharValue(txtDoorIP.getText());
        saveOption(GlobalOption.doorServerIp);
        GlobalOption.doorServerPort.setIntValue(getInteger(txtDoorPort));
        saveOption(GlobalOption.doorServerPort);
    }
}
