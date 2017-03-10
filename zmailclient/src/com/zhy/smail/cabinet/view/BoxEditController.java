package com.zhy.smail.cabinet.view;

import com.zhy.smail.MainApp;
import com.zhy.smail.cabinet.entity.BoxInfo;
import com.zhy.smail.cabinet.service.BoxService;
import com.zhy.smail.common.controller.RootController;
import com.zhy.smail.component.SimpleDialog;
import com.zhy.smail.component.TimeoutTimer;
import com.zhy.smail.config.GlobalOption;
import com.zhy.smail.lcp.LcProtocol;
import com.zhy.smail.lcp.LcResult;
import com.zhy.smail.lcp.command.LcCommand;
import com.zhy.smail.restful.RestfulResult;
import com.zhy.smail.restful.RfFaultEvent;
import com.zhy.smail.restful.RfResultEvent;
import com.zhy.smail.task.*;
import com.zhy.smail.user.entity.UserInfo;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by wenliz on 2017/2/11.
 */
public class BoxEditController extends RootController  implements Initializable{
    private final static String SELECTED_CLASS="selected_toggle";


    private BoxInfo boxInfo;

    @FXML
    private Label lblTimer;
    @FXML
    private Label lblSequence;
    @FXML
    private ToggleButton btnBoxMail;
    @FXML
    private ToggleButton btnBoxSmall;
    @FXML
    private ToggleButton btnBoxMiddle;
    @FXML
    private ToggleButton btnBoxLarge;
    @FXML
    private Label lblOpened;
    @FXML
    private Label lblLocked;
    @FXML
    private Label lblUseStatus;
    @FXML
    private Button openButton;
    @FXML
    private Button lockButton;
    @FXML
    private Button clearButton;
    private Timer dector;

    public BoxInfo getBoxInfo() {
        return boxInfo;
    }

    public void setBoxInfo(BoxInfo boxInfo) {
        this.boxInfo = boxInfo;
        assignBox(boxInfo);
    }



    public void setApp(MainApp app) {
        this.app = app;
        app.createTimeout(lblTimer);
        if(!GlobalOption.currentUser.getUserType().equals(UserInfo.FACTORY_USER)){
            btnBoxMail.setDisable(true);
            btnBoxSmall.setDisable(true);
            btnBoxLarge.setDisable(true);
            btnBoxMiddle.setDisable(true);
        }
        if(GlobalOption.currentCabinet.getCabinetId().equals(boxInfo.getCabinetId())) {
            GetBoxStatusTask task = new GetBoxStatusTask(boxInfo);
            task.valueProperty().addListener(new ChangeListener<Integer>() {
                @Override
                public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                    if (newValue >= 0) {
                        if (newValue == 0) {
                            setOpened(false);
                        } else if (newValue > 0) {
                            setOpened(true);
                        }
                        saveBox();
                    }
                }
            });
            SimpleDialog.showDialog(app.getRootStage(), task, "正在检查箱门状态...", "");
        }
        else{
            openButton.setDisable(true);
            clearButton.setDisable(true);
        }

    }

    public void initialize(URL location, ResourceBundle resources){

    }

    private void assignBox(BoxInfo boxInfo){
        lblSequence.setText(boxInfo.getSequence().toString());
        setOpened(boxInfo.isOpened());
        setUsed(boxInfo.isUsed());
        setLocked(boxInfo.isLocked());

        if(boxInfo.getCabinetId() == GlobalOption.currentCabinet.getCabinetId()){
            //clearButton.setDisable(false);
        }
        else{
           // clearButton.setDisable(true);
        }

        changeToType(boxInfo.getBoxType());
    }

    @FXML
    private void onBoxTypeChange(ActionEvent event){
        ToggleButton button = (ToggleButton)event.getTarget();
        if(button== btnBoxMiddle){
            changeToType(BoxInfo.BOX_TYPE_MIDDLE);
            boxInfo.setBoxType(BoxInfo.BOX_TYPE_MIDDLE);
        }
        else if(button == btnBoxLarge){
            changeToType(BoxInfo.BOX_TYPE_LARGE);
            boxInfo.setBoxType(BoxInfo.BOX_TYPE_LARGE);
        }
        else if(button == btnBoxMail){
            changeToType(BoxInfo.BOX_TYPE_MAIL);
            boxInfo.setBoxType(BoxInfo.BOX_TYPE_MAIL);
        }
        else if(button == btnBoxSmall){
            changeToType(BoxInfo.BOX_TYPE_SMALL);
            boxInfo.setBoxType(BoxInfo.BOX_TYPE_SMALL);
        }
        saveBox();
    }

    private void saveBox(){
        BoxService.save(boxInfo, new RestfulResult() {
            @Override
            public void doResult(RfResultEvent event) {

            }

            @Override
            public void doFault(RfFaultEvent event) {

            }
        });
    }

    private void changeToType(int boxType){
        removeSelectedClass();
        switch (boxType){
            case BoxInfo.BOX_TYPE_MAIL:
                btnBoxMail.setSelected(true);
                btnBoxMail.getStyleClass().add(SELECTED_CLASS);
                break;
            case BoxInfo.BOX_TYPE_SMALL:
                btnBoxSmall.setSelected(true);
                btnBoxSmall.getStyleClass().add(SELECTED_CLASS);
                break;
            case BoxInfo.BOX_TYPE_MIDDLE:
                btnBoxMiddle.setSelected(true);
                btnBoxMiddle.getStyleClass().add(SELECTED_CLASS);
                break;
            case BoxInfo.BOX_TYPE_LARGE:
                btnBoxLarge.setSelected(true);
                btnBoxLarge.getStyleClass().add(SELECTED_CLASS);
                break;
        }
    }

    private void removeSelectedClass(){
        btnBoxMiddle.getStyleClass().remove(SELECTED_CLASS);
        btnBoxLarge.getStyleClass().remove(SELECTED_CLASS);
        btnBoxMail.getStyleClass().remove(SELECTED_CLASS);
        btnBoxSmall.getStyleClass().remove(SELECTED_CLASS);
    }

    @FXML
    private void onBackAction(ActionEvent event){
        if(dector != null){
            dector.cancel();
            dector = null;
        }
        BoxListController controller = app.goBoxList();
        controller.createCabinetList(boxInfo.getCabinetId());
    }

    @FXML
    private void onOpenBoxAction(ActionEvent event){
        OpenBoxTask task = new OpenBoxTask(boxInfo, "正在打开箱门...");
        task.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                if(newValue!=null && newValue == 0){
                    setOpened(true);
                    saveBox();
                    //SimpleDialog.showMessageDialog(app.getRootStage(), "箱门已经打开，请随手关门.","信息");
                    //detectOpenDoor();
                }
            }
        });
        SimpleDialog.showDialog(app.getRootStage(), task, "", "");
    }

    @FXML
    private void onLockBoxAction(ActionEvent event){
        boolean locked = true;
        if(boxInfo.isLocked()){
            locked = false;
        }
        setLocked(locked);

        saveBox();
    }

    private void setLocked(boolean locked){
        boxInfo.setLocked(locked);
        if(boxInfo.isLocked()){
            lblLocked.setText("锁定");
            lockButton.setText("解锁");
        }
        else{
            lblLocked.setText("未锁定");
            lockButton.setText("锁定");
        }
    }

    private void setOpened(boolean opened){
        boxInfo.setOpened(opened);
        if(boxInfo.isOpened()){
            lblOpened.setText("打开");
            //openButton.setDisable(true);
        }
        else{
            lblOpened.setText("关闭");
            //openButton.setDisable(false);
        }
    }

    private void setUsed(boolean used){
        boxInfo.setUsed(used);
        if(boxInfo.isUsed()){
            lblUseStatus.setText("占用");
        }
        else{
            lblUseStatus.setText("空闲");
        }
    }

    private void detectOpenDoor(){
        DectCloseDoor task = new DectCloseDoor(boxInfo);
        task.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                if(newValue!=null && newValue==0){
                    setOpened(false);
                    saveBox();
                }
            }
        });
        SimpleDialog.showDialog(app.getRootStage(), task, "","");
    }

    @FXML
    private void onClearBoxAction(ActionEvent event){
        if(boxInfo.isLocked()){
            SimpleDialog.showMessageDialog(app.getRootStage(), "锁定的箱门不能清箱。","错误");
            return;
        }

        OpenBoxTask task = new OpenBoxTask(boxInfo, "正在清箱...");
        task.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                if(newValue == 0){
                    setOpened(true);
                    setUsed(false);

                    saveBox();
                   // SimpleDialog.showMessageDialog(app.getRootStage(), "清箱成功。箱门已打开，请随手关好箱门.","信息");
                    //detectOpenDoor();
                }
            }
        });
        SimpleDialog.showDialog(app.getRootStage(), task, "正在清箱...", "清箱");


    }

}
