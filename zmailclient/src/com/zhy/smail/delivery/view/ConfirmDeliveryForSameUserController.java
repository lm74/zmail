package com.zhy.smail.delivery.view;

import com.zhy.smail.MainApp;
import com.zhy.smail.cabinet.entity.BoxInfo;
import com.zhy.smail.common.controller.RootController;
import com.zhy.smail.component.SimpleDialog;
import com.zhy.smail.config.GlobalOption;
import com.zhy.smail.manager.entity.DeliveryLog;
import com.zhy.smail.manager.service.DeliveryLogService;
import com.zhy.smail.restful.RestfulResult;
import com.zhy.smail.restful.RfFaultEvent;
import com.zhy.smail.restful.RfResultEvent;
import com.zhy.smail.user.entity.UserInfo;
import com.zhy.smail.user.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.net.URL;
import java.util.*;

public class ConfirmDeliveryForSameUserController extends RootController implements Initializable {
    @FXML
    private Label lblTimer;
    @FXML
    private Region topLeft;
    @FXML
    private Region topRight;
    private List<UserInfo> userInfoList;
    private BoxInfo box;
    private Map<String, Button> buttonMap = new HashMap<>();
    @FXML
    private FlowPane boxesFlow;

    public void setApp(MainApp app) {
        this.app = app;
        app.createTimeout(lblTimer);
        List userList = findUserInfoByNoPickupMail();
        if (userList != null && userList.size() > 0) {
            setUserInfoList(userList);
        } else {
            onBackAction(null);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        boxesFlow.getChildren().remove(0, boxesFlow.getChildren().size());
        boxesFlow.setHgap(10);
        boxesFlow.setVgap(10);
        HBox.setHgrow(topLeft, Priority.ALWAYS);
        HBox.setHgrow(topRight, Priority.ALWAYS);
    }

    @FXML
    public void onBackAction(ActionEvent event) {
        app.goDelivery();
    }

    public List<UserInfo> getUserInfoList() {
        return userInfoList;
    }

    // 得到未取信件的用户信息
    public List findUserInfoByNoPickupMail() {
        List user = new ArrayList<UserInfo>();
        UserService.findUserInfoByNoPickupMail(new RestfulResult() {
            @Override
            public void doResult(RfResultEvent event) {
                if (event.getResult() == RfResultEvent.OK && event.getData() != null) {
                    List<UserInfo> userList = (List<UserInfo>) event.getData();
                    for (int i = 0; i < userList.size(); i++) {
                        UserInfo userInfo = (UserInfo) userList.get(i);
                        user.add(userInfo);
                    }
                }
            }

            @Override
            public void doFault(RfFaultEvent event) {

            }
        });
        return user;
    }


    public void setUserInfoList(List<UserInfo> userInfoList) {
        buttonMap.clear();
        this.userInfoList = userInfoList;
        onRefreshUser();
    }

    private void onRefreshUser() {
        for (int i = 0; i < userInfoList.size(); i++) {
            UserInfo userInfo = userInfoList.get(i);
            Button button = createButton(userInfo);
        }
    }

    private Button createButton(UserInfo userInfo) {
        Button button = createOneButton(userInfo);
        boxesFlow.getChildren().add(button);
        button.addEventHandler(ActionEvent.ACTION, (ActionEvent actionEvent) -> {
            DeliveryLogService.listByOwner(GlobalOption.currentCabinet.getCabinetId(),
                    userInfo.getUserId(), 0, new RestfulResult() {
                        @Override
                        public void doResult(RfResultEvent event) {
                            if (!event.getResult().equals(RfResultEvent.OK)) {
                                return;
                            }
                            Integer type = 0;
                            int boxNumber = 0;
                            List<DeliveryLog> logs = (List<DeliveryLog>) event.getData();
                            if (logs == null || logs.size() == 0) {
                                return;
                            }else{
                                for(int i = 0; i < logs.size();i++){
                                    type = logs.get(i).getDeliveryType();
                                    if(0 == type){
                                        BoxInfo boxInfo = logs.get(i).getBoxInfo();
                                        startDelivery(boxInfo, userInfo);
                                        break;
                                    }else{
                                        boxNumber++;
                                    }
                                }
                                if(boxNumber == logs.size()){
                                    button.setDisable(true);
                                    SimpleDialog.showMessageDialog(app.getRootStage(), "箱门已装满包裹，不能投递信件！.","");
                                    return;
                                }
                            }

                            /*List<DeliveryLog> logs = (List<DeliveryLog>) event.getData();
                            if (logs == null || logs.size() == 0) {
                                return;
                            }
                            BoxInfo boxInfo = logs.get(0).getBoxInfo();
                            startDelivery(boxInfo, userInfo);*/
                        }

                        @Override
                        public void doFault(RfFaultEvent event) {

                        }
                    });

        });
        return button;
    }

    public void startDelivery(BoxInfo box, UserInfo user) {
        if (box == null) {
            SimpleDialog.showMessageDialog(app.getRootStage(), "请选择空闲的箱门类别.", "");
            return;
        }
        if (user == null) {
            SimpleDialog.showMessageDialog(app.getRootStage(), "请输入或者选择房号。", "");
            return;
        }
        ConfirmDeliveryController controller = app.goConfirmDelivery();
        controller.setUser(user);
        controller.setBox(box);
        GlobalOption.parents.push("putmailf");
    }

    private Button createOneButton(UserInfo userInfo) {
        String title = userInfo.getUserName();
        int height = 60;
        Image image;
        image = new Image(getClass().getResourceAsStream("/resources/images/button/email.png"));
        ImageView imageView = new ImageView();
        imageView.setImage(image);
        Button button = new Button(title, imageView);
        button.setPrefWidth(300);
        button.setPrefHeight(height);
        return button;
    }
}
