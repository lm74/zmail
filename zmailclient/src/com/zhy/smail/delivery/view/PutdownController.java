package com.zhy.smail.delivery.view;

import com.sun.corba.se.spi.orbutil.fsm.Action;
import com.zhy.smail.MainApp;
import com.zhy.smail.cabinet.entity.BoxInfo;
import com.zhy.smail.cabinet.entity.CabinetEntry;
import com.zhy.smail.cabinet.service.BoxService;
import com.zhy.smail.common.controller.RootController;
import com.zhy.smail.component.SimpleDialog;
import com.zhy.smail.component.music.Speaker;
import com.zhy.smail.config.GlobalOption;
import com.zhy.smail.delivery.service.SqlSuggestionProvider;
import com.zhy.smail.lcp.BoxEntry;
import com.zhy.smail.lcp.LcProtocol;
import com.zhy.smail.lcp.LcResult;
import com.zhy.smail.lcp.command.LcCommand;
import com.zhy.smail.restful.RestfulResult;
import com.zhy.smail.restful.RfFaultEvent;
import com.zhy.smail.restful.RfResultEvent;
import com.zhy.smail.task.GetCabinetStatus;
import com.zhy.smail.task.ResponseManager;
import com.zhy.smail.task.SendManager;
import com.zhy.smail.user.entity.UserInfo;
import impl.org.controlsfx.autocompletion.AutoCompletionTextFieldBinding;
import impl.org.controlsfx.autocompletion.SuggestionProvider;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.shape.Box;
import javafx.util.Callback;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.CustomTextField;
import org.controlsfx.control.textfield.TextFields;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by wenliz on 2017/2/16.
 */
public class PutdownController  extends RootController implements Initializable {

    @FXML
    private Label lblTimer;
    @FXML
    private Region topLeft;
    @FXML
    private Region topRight;
    @FXML
    private TextField txtRoomNo;


    private UserInfo user;

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
        txtRoomNo.setText(user.getUserName());
    }

    private List<BoxInfo> smallBoxes;
    private List<BoxInfo> middleBoxes;
    private List<BoxInfo> largeBoxes;

    @FXML
    private ToggleButton smallButton;
    @FXML
    private ToggleButton middleButton;
    @FXML
    private ToggleButton largeButton;
    private BoxInfo box;



    public void setApp(MainApp app) {
        this.app = app;
        app.createTimeout(lblTimer);

        listAvailableBox();
    }

    public void checkBoxStatus(){
        BoxService.listByCabinetId(GlobalOption.currentCabinet.getCabinetId(), new RestfulResult() {
            @Override
            public void doResult(RfResultEvent event) {
                if(event.getResult()!=RfResultEvent.OK) return;

                List<BoxInfo> boxes = (List<BoxInfo>) event.getData();

                CabinetEntry cabinetEntry = new CabinetEntry();
                for(int i=0; i<boxes.size(); i++){
                    BoxInfo boxInfo = boxes.get(i);

                    cabinetEntry.addBox(boxInfo);
                }

                GetCabinetStatus task = new GetCabinetStatus(cabinetEntry);
                task.valueProperty().addListener(new ChangeListener<Integer>() {
                    @Override
                    public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                        if(newValue != 0) return;

                        boolean hasOpened = false;
                        String openNos = "";
                        for(int i=0; i<boxes.size(); i++) {
                            BoxInfo boxInfo = boxes.get(i);
                            BoxEntry boxEntry = task.getCabinet().getBoxEntry(boxInfo.getControlCardId(), boxInfo.getControlSequence());
                            if (boxEntry == null) continue;

                            if(boxEntry.getStatus()==0 && boxInfo.isOpened()){
                                boxInfo.setOpened(false);
                                saveBox(boxInfo);
                            }
                            else if(boxEntry.getStatus() == 1 && !boxInfo.isOpened()){
                                boxInfo.setOpened(true);
                                saveBox(boxInfo);
                            }

                            if (!boxInfo.isLocked() && boxInfo.isOpened()) {
                                hasOpened = true;
                                openNos += "," +boxInfo.getSequence();
                            }
                        }
                        if(hasOpened){
                            openNos = openNos.substring(1);
                            SimpleDialog.showMessageDialog(app.getRootStage(), "请关上打开的箱门:"+openNos+"，否则不能投递.","");
                            smallButton.setDisable(true);
                            middleButton.setDisable(true);
                            largeButton.setDisable(true);
                            app.goDelivery();
                        }
                    }

                });
                SimpleDialog.showDialog(app.getRootStage(), task, "", "");



            }

            @Override
            public void doFault(RfFaultEvent event) {

            }
        });

    }

    public void initialize(URL location, ResourceBundle resources) {

        smallBoxes = new ArrayList<>();
        middleBoxes = new ArrayList<>();
        largeBoxes = new ArrayList<>();
        user = null;
        box = null;

        HBox.setHgrow(topLeft, Priority.ALWAYS);
        HBox.setHgrow(topRight, Priority.ALWAYS);
        createUserAutoCombBox(txtRoomNo);
        txtRoomNo.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(newValue){
                    Speaker.choseRoomNo();
                }
            }
        });


    }

    private void listAvailableBox(){

        smallBoxes.clear();
        middleBoxes.clear();
        largeBoxes.clear();
        BoxService.listAvailableBox(GlobalOption.currentCabinet.getCabinetId(), new RestfulResult() {
            @Override
            public void doResult(RfResultEvent event) {
                if(event.getResult()!=RfResultEvent.OK) return;

                List<BoxInfo> boxes = (List<BoxInfo>) event.getData();
                for(int i=0; i<boxes.size(); i++){
                    BoxInfo box = boxes.get(i);
                    switch (box.getBoxType()){
                        case BoxInfo.BOX_TYPE_SMALL:
                            smallBoxes.add(box);
                            break;
                        case BoxInfo.BOX_TYPE_MIDDLE:
                            middleBoxes.add(box);
                            break;
                        case BoxInfo.BOX_TYPE_LARGE:
                            largeBoxes.add(box);
                            break;
                    }
                }
                setButtonsText();



            }

            @Override
            public void doFault(RfFaultEvent event) {

            }
        });
    }

    private void setButtonsText(){

        setButtonText(smallButton, "小包箱", smallBoxes);
        setButtonText(middleButton, "中包箱", middleBoxes);
        setButtonText(largeButton, "大包箱", largeBoxes);
        int count = smallBoxes.size() + middleBoxes.size()+largeBoxes.size();
        if(count >0) return;

        String nodesTypes = BoxInfo.BOX_TYPE_SMALL+","+BoxInfo.BOX_TYPE_MIDDLE+","+BoxInfo.BOX_TYPE_LARGE;
        BoxService.getAnotherMaxAvailableCabinet(GlobalOption.currentCabinet.getCabinetId(), nodesTypes, new RestfulResult() {
            @Override
            public void doResult(RfResultEvent event) {
                if(event.getResult()>0 ){
                    Integer count = Integer.valueOf(event.getData().toString());
                    if(count == 0){
                        SimpleDialog.showMessageDialog(app.getRootStage(), "全部箱门已满，不能投件.", "");
                    }
                    else {
                        String message = "本柜箱门已满," + event.getResult() + "号柜有" + event.getData().toString() + "个空箱,请到" + event.getResult() + "号柜投件.";
                        SimpleDialog.showMessageDialog(app.getRootStage(), message, "");

                    }
                }
            }

            @Override
            public void doFault(RfFaultEvent event) {

            }
        });
    }

    private void setButtonText(ToggleButton button, String title, List<BoxInfo> boxes){
        button.setText(title+"(" + boxes.size()+")");
        if(boxes.size() == 0){
            button.setDisable(true);
        }
        else{
            button.setDisable(false);
        }
    }

    protected void  changeUser(UserInfo user){
        this.user = user;
    }

    @FXML
    public void onToggleAction(ActionEvent event){
       if(event.getTarget() == smallButton && smallBoxes.size()>0){
            box = smallBoxes.get(0);
        }
        else if(event.getTarget() == middleButton && middleBoxes.size()>0){
            box = middleBoxes.get(0);
        }
        else if(event.getTarget() == largeButton && largeBoxes.size() >0){
            box = largeBoxes.get(0);
        }

        if(box == null){
            SimpleDialog.showMessageDialog(app.getRootStage(),"请选择空闲的箱门类别.", "");
            return;
        }
        if(user == null){
            SimpleDialog.showMessageDialog(app.getRootStage(), "请选择有效的房号或手机号码.","");
            return;
        }


        ConfirmDeliveryController controller = app.goConfirmDelivery();
        controller.setUser(user);
        controller.setBox(box);


        GlobalOption.parents.push("putdown");


    }

    @FXML
    public void onChoiceUserAction(ActionEvent event){
        GlobalOption.parents.push("putdown");
        app.goUserChoice();
    }

    @FXML
    public void onBackAction(ActionEvent event) {//一律返回邮政投递界面？
        app.goDelivery();
    }
    private void saveBox(BoxInfo boxInfo){
        BoxService.save(boxInfo, new RestfulResult() {
            @Override
            public void doResult(RfResultEvent event) {

            }

            @Override
            public void doFault(RfFaultEvent event) {

            }
        });
    }
}
