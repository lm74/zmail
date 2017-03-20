package com.zhy.smail.user.view;

import com.zhy.smail.MainApp;
import com.zhy.smail.cabinet.entity.BoxInfo;
import com.zhy.smail.common.controller.RootController;
import com.zhy.smail.component.SimpleDialog;
import com.zhy.smail.component.keyboard.control.VkProperties;
import com.zhy.smail.config.GlobalOption;
import com.zhy.smail.delivery.view.PutdownController;
import com.zhy.smail.delivery.view.PutmailController;
import com.zhy.smail.delivery.view.SelectRoomController;
import com.zhy.smail.restful.RestfulResult;
import com.zhy.smail.restful.RfFaultEvent;
import com.zhy.smail.restful.RfResultEvent;
import com.zhy.smail.user.entity.UserInfo;
import com.zhy.smail.user.service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by wenliz on 2017/2/22.
 */
public class UserChoiceController extends RootController implements Initializable {
    @FXML
    private Label lblTimer;
    @FXML
    private ToolBar topToolBar;
    @FXML
    private Region topLeft;
    @FXML
    private Region topRight;

    @FXML
    private TableView<UserInfo> ownerTable;
    private ObservableList ownerList;
    @FXML
    private TableColumn<UserInfo, String> tcBuildingNo;
    @FXML
    private TableColumn<UserInfo, String> tcUnitNo;
    @FXML
    private TableColumn<UserInfo, String> tcFloorNo;
    @FXML
    private TableColumn<UserInfo, String> tcRoomNo;
    @FXML
    private TableColumn<UserInfo, String> tcPhoneNo;
    @FXML
    private TableColumn<UserInfo, String> tcCardNo1;
    @FXML
    private TableColumn<UserInfo, String> tcCardNo2;
    @FXML
    private TableColumn<UserInfo, String> tcCardNo3;
    @FXML
    private TableColumn<UserInfo, String> tcCardNo4;
    @FXML
    private TableColumn<UserInfo, String> tcCardNo5;
    @FXML
    private TableColumn<UserInfo, String> tcCardNo6;
    @FXML
    private TableColumn<UserInfo, String> tcCardNo7;
    @FXML
    private TableColumn<UserInfo, String> tcCardNo8;
    @FXML
    private TableColumn<UserInfo, String> tcCardNo9;
    @FXML
    private TableColumn<UserInfo, String> tcCardNo10;

    @FXML
    private TextField txtBuildingNo;
    @FXML
    private TextField txtUnitNo;
    @FXML
    private TextField txtFloorNo;
    @FXML
    private TextField txtRoomNo;
    private BoxInfo box;

    public BoxInfo getBox() {
        return box;
    }

    public void setBox(BoxInfo box) {
        this.box = box;
    }

    public void setApp(MainApp app) {
        this.app = app;
        app.createTimeout(lblTimer);
    }

    public void initialize(URL location, ResourceBundle resources) {
        HBox.setHgrow(topLeft, Priority.ALWAYS);
        HBox.setHgrow(topRight, Priority.ALWAYS);
        txtBuildingNo.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_NUMERIC);
        txtUnitNo.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_NUMERIC);
        txtFloorNo.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_NUMERIC);
        txtRoomNo.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_NUMERIC);
        createOwnerTable();
        onRefresh();
    }


    private void createOwnerTable() {
        tcBuildingNo.setCellValueFactory(new PropertyValueFactory("buildingNo"));
        tcUnitNo.setCellValueFactory(new PropertyValueFactory<UserInfo, String>("unitNo"));
        tcFloorNo.setCellValueFactory(new PropertyValueFactory<UserInfo, String>("floorNo"));
        tcRoomNo.setCellValueFactory(new PropertyValueFactory<UserInfo, String>("roomNo"));
        tcPhoneNo.setCellValueFactory(new PropertyValueFactory<UserInfo, String>("phoneNo"));
        ownerList = FXCollections.observableArrayList();
        ownerTable.setItems(ownerList);
    }

    public void onRefresh() {
        ownerList.clear();
        UserService.listOwnerByRoom(txtBuildingNo.getText(), txtUnitNo.getText(), txtFloorNo.getText(), txtRoomNo.getText(),
                new RestfulResult() {
                    @Override
                    public void doResult(RfResultEvent event) {
                        List<UserInfo> users = (List<UserInfo>) event.getData();
                        if (users == null) return;
                        String phone = "";
                        for (int i = 0; i < users.size(); i++) {
                            UserInfo user = users.get(i);
                            if(user.getPhoneNo() != null){
                                phone = user.getPhoneNo().replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
                            }
                            user.setPhoneNo(phone);
                            ownerList.add(user);
                        }
                    }

                    @Override
                    public void doFault(RfFaultEvent event) {

                    }
                });
    }

    public void onQueryAction(ActionEvent event) {
        onRefresh();
    }

    @FXML
    public void onOkAction(ActionEvent event) {
        UserInfo user = ownerTable.getSelectionModel().getSelectedItem();
        if (user == null) {
            SimpleDialog.showMessageDialog(app.getRootStage(), "请选择用户.", "删除出错");
            return;
        }
        backToParent(user);
    }

    @FXML
    private void onBackAction(ActionEvent event) {
        backToParent(null);
    }

    private void backToParent(UserInfo user) {
        String parent = GlobalOption.parents.pop();
        if (parent != null && parent.equals("putmail")) {
            PutmailController controller = app.goPutmail();
            if (controller != null && user != null) {
                controller.setUser(user);
            }
        } else if (parent != null && parent.equals("selectRoom")) {
            SelectRoomController controller = app.goSelectRoom();
            if (controller != null && user != null) {
                controller.setUser(user);
                controller.setBox(box);
            } else if (controller != null && user == null) {
                controller.setBox(box);
            }
        } else {
            PutdownController controller = app.goPutdown();
            if (controller != null && user != null) {
                controller.setUser(user);
            }
        }
    }
}
