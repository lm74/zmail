package com.zhy.smail.delivery.view;

import com.zhy.smail.MainApp;
import com.zhy.smail.cabinet.entity.BoxInfo;
import com.zhy.smail.cabinet.entity.CabinetEntry;
import com.zhy.smail.cabinet.service.BoxService;
import com.zhy.smail.common.controller.RootController;
import com.zhy.smail.component.SimpleDialog;
import com.zhy.smail.component.music.Speaker;
import com.zhy.smail.config.GlobalOption;
import com.zhy.smail.lcp.BoxEntry;
import com.zhy.smail.restful.RestfulResult;
import com.zhy.smail.restful.RfFaultEvent;
import com.zhy.smail.restful.RfResultEvent;
import com.zhy.smail.task.GetCabinetStatus;
import com.zhy.smail.user.entity.UserInfo;
import com.zhy.smail.user.view.UserChoiceController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Administrator on 2017/3/11.
 */
public class SelectRoomController  extends RootController implements Initializable {

    @FXML
    private Label lblTimer;
    @FXML
    private Region topLeft;
    @FXML
    private Region topRight;
    @FXML
    private TextField txtRoomNo;
    @FXML
    private  Label lblBoxNo;


    private UserInfo user;

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
        txtRoomNo.setText(user.getUserName());
    }


    private BoxInfo box;

    public BoxInfo getBox() {
        return box;
    }

    public void setBox(BoxInfo box) {
        this.box = box;

        lblBoxNo.setText(String.valueOf(box.getSequence()));
    }

    public void setApp(MainApp app) {
        this.app = app;
        app.createTimeout(lblTimer);


    }



    public void initialize(URL location, ResourceBundle resources) {
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



    protected void  changeUser(UserInfo user){
        this.user = user;
    }

    @FXML
    public void onOpenAction(ActionEvent event){
       if(user == null){
            SimpleDialog.showMessageDialog(app.getRootStage(), "请选择有效的房号或手机号码.","");
            return;
        }


        ConfirmDeliveryController controller = app.goConfirmDelivery();
        controller.setUser(user);
        controller.setBox(box);


        GlobalOption.parents.push("selectRoom");


    }

    @FXML
    public void onChoiceUserAction(ActionEvent event){
        GlobalOption.parents.push("selectRoom");
        UserChoiceController controller = app.goUserChoice();
        controller.setBox(box);
    }

    @FXML
    public void onBackAction(ActionEvent event){
        app.goCommonDelivery();
    }

}
