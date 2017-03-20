package com.zhy.smail.user.view;

import com.sun.corba.se.spi.orbutil.fsm.Action;
import com.zhy.smail.MainApp;
import com.zhy.smail.component.SimpleDialog;
import com.zhy.smail.config.GlobalOption;
import com.zhy.smail.restful.RestfulResult;
import com.zhy.smail.restful.RfFaultEvent;
import com.zhy.smail.restful.RfResultEvent;
import com.zhy.smail.setting.service.OptionService;
import com.zhy.smail.task.ExportUserTask;
import com.zhy.smail.task.ImportUserTask;
import com.zhy.smail.user.entity.UserInfo;
import com.zhy.smail.user.service.UserService;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by wenliz on 2017/1/16.
 */
public class UserListController implements Initializable {
    private MainApp app;
    @FXML
    private Label lblTimer;
    @FXML
    private ToolBar topToolBar;
    @FXML
    private Region topLeft;
    @FXML
    private Region topRight;

    public MainApp getApp() {
        return app;
    }

    public void setApp(MainApp app) {
        this.app = app;
        app.createTimeout(lblTimer);
    }

    @FXML
    private TabPane userContainer;
    @FXML
    private Tab ownerTab;
    @FXML
    private Tab deliveryTab;
    @FXML
    private Tab managerTab;

    @FXML
    private TableView<UserInfo> ownerTable;
    private ObservableList<UserInfo> ownerList;
    @FXML
    private TableColumn<UserInfo, Boolean> tcChecked;
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
    private TableView<UserInfo> deliveryTable;
    private ObservableList<UserInfo> deliveryList;
    @FXML
    private TableColumn<UserInfo, Boolean> tcdChecked;
    @FXML
    private TableColumn<UserInfo, String> tcDelivery;
    @FXML
    private TableColumn<UserInfo, String> tcdPhoneNo;
    @FXML
    private TableColumn<UserInfo, String> tcdCardNo1;
    @FXML
    private TableColumn<UserInfo, String> tcdCardNo2;
    @FXML
    private TableColumn<UserInfo, String> tcdCardNo3;
    @FXML
    private TableColumn<UserInfo, String> tcdCardNo4;
    @FXML
    private TableColumn<UserInfo, String> tcdCardNo5;
    @FXML
    private TableColumn<UserInfo, String> tcdCardNo6;
    @FXML
    private TableColumn<UserInfo, String> tcdCardNo7;
    @FXML
    private TableColumn<UserInfo, String> tcdCardNo8;
    @FXML
    private TableColumn<UserInfo, String> tcdCardNo9;
    @FXML
    private TableColumn<UserInfo, String> tcdCardNo10;
    @FXML
    private TableView<UserInfo> managerTable;
    private ObservableList<UserInfo> managerList;
    @FXML
    private TableColumn<UserInfo, String> tcUserName;
    @FXML
    private TableColumn<UserInfo, Boolean> tcmChecked;
    @FXML
    private TableColumn<UserInfo, String> tcmPhoneNo;
    @FXML
    private TableColumn<UserInfo, String> tcmCardNo1;
    @FXML
    private TableColumn<UserInfo, String> tcmCardNo2;
    @FXML
    private TableColumn<UserInfo, String> tcmCardNo3;
    @FXML
    private TableColumn<UserInfo, String> tcmCardNo4;
    @FXML
    private TableColumn<UserInfo, String> tcmCardNo5;
    @FXML
    private TableColumn<UserInfo, String> tcmCardNo6;
    @FXML
    private TableColumn<UserInfo, String> tcmCardNo7;
    @FXML
    private TableColumn<UserInfo, String> tcmCardNo8;
    @FXML
    private TableColumn<UserInfo, String> tcmCardNo9;
    @FXML
    private TableColumn<UserInfo, String> tcmCardNo10;
    @FXML
    private CheckBox chkSelectAll;
    @FXML
    private Button updateRecord;
    @FXML
    private Button setPassword;

    private FileChooser openFileChoose;
    private FileChooser saveFileChoose;

    public void onRefresh() {
        ownerList.clear();
        managerList.clear();
        deliveryList.clear();
        UserService.listAll(new RestfulResult() {
            @Override
            public void doResult(RfResultEvent event) {
                List<UserInfo> users = (List<UserInfo>) event.getData();
                if (users == null) return;

                Integer currentUserType = GlobalOption.currentUser.getUserType();
                for (int i = 0; i < users.size(); i++) {
                    UserInfo user = users.get(i);
                    if (user.getUserType() == UserInfo.OWNER) {
                        ownerList.add(user);
                    } else if (user.getUserType() == UserInfo.DELIVERY || user.getUserType() == UserInfo.MAILMAN) {
                        deliveryList.add(user);
                    } else {
                        if (user.getUserType() >= currentUserType) {
                            managerList.add(user);
                        }
                    }
                }
            }

            @Override
            public void doFault(RfFaultEvent event) {

            }
        });
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initFileChooser();
        HBox.setHgrow(topLeft, Priority.ALWAYS);
        HBox.setHgrow(topRight, Priority.ALWAYS);
        createOwnerTable();
        createManagerTable();
        createDeliveryTable();
        onRefresh();


    }

    private void initFileChooser() {
        openFileChoose = new FileChooser();
        openFileChoose.setTitle("选择输入文件(XLS)");
        openFileChoose.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel文件", "*.xls"));

        saveFileChoose = new FileChooser();
        saveFileChoose.setTitle("输入输出文件名(XLS)");
        saveFileChoose.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel文件", "*.xls"));
    }

    private void createOwnerTable() {
        tcChecked.setCellFactory(tc -> new CheckBoxTableCell<UserInfo, Boolean>());
        tcChecked.setCellValueFactory(new PropertyValueFactory<UserInfo, Boolean>("checked"));
        tcBuildingNo.setCellValueFactory(new PropertyValueFactory("buildingNo"));
        tcUnitNo.setCellValueFactory(new PropertyValueFactory<UserInfo, String>("unitNo"));
        tcFloorNo.setCellValueFactory(new PropertyValueFactory<UserInfo, String>("floorNo"));
        tcRoomNo.setCellValueFactory(new PropertyValueFactory<UserInfo, String>("roomNo"));
        tcPhoneNo.setCellValueFactory(new PropertyValueFactory<UserInfo, String>("phoneNo"));
        tcCardNo1.setCellValueFactory(new PropertyValueFactory<UserInfo, String>("cardNo1"));
        tcCardNo2.setCellValueFactory(new PropertyValueFactory<UserInfo, String>("cardNo2"));
        tcCardNo3.setCellValueFactory(new PropertyValueFactory<UserInfo, String>("cardNo3"));
        tcCardNo4.setCellValueFactory(new PropertyValueFactory<UserInfo, String>("cardNo4"));
        tcCardNo5.setCellValueFactory(new PropertyValueFactory<UserInfo, String>("cardNo5"));
        tcCardNo6.setCellValueFactory(new PropertyValueFactory<UserInfo, String>("cardNo6"));
        tcCardNo7.setCellValueFactory(new PropertyValueFactory<UserInfo, String>("cardNo7"));
        tcCardNo8.setCellValueFactory(new PropertyValueFactory<UserInfo, String>("cardNo8"));
        tcCardNo9.setCellValueFactory(new PropertyValueFactory<UserInfo, String>("cardNo9"));
        tcCardNo10.setCellValueFactory(new PropertyValueFactory<UserInfo, String>("cardNo10"));
        ownerList = FXCollections.observableArrayList();

        ownerTable.setItems(ownerList);

    }

    private void createManagerTable() {
        tcdChecked.setCellFactory(tc -> new CheckBoxTableCell<UserInfo, Boolean>());
        tcdChecked.setCellValueFactory(new PropertyValueFactory<UserInfo, Boolean>("checked"));
        tcUserName.setCellValueFactory(new PropertyValueFactory("userName"));
        tcmPhoneNo.setCellValueFactory(new PropertyValueFactory<UserInfo, String>("phoneNo"));
        tcmCardNo1.setCellValueFactory(new PropertyValueFactory<UserInfo, String>("cardNo1"));
        tcmCardNo2.setCellValueFactory(new PropertyValueFactory<UserInfo, String>("cardNo2"));
        tcmCardNo3.setCellValueFactory(new PropertyValueFactory<UserInfo, String>("cardNo3"));
        tcmCardNo4.setCellValueFactory(new PropertyValueFactory<UserInfo, String>("cardNo4"));
        tcmCardNo5.setCellValueFactory(new PropertyValueFactory<UserInfo, String>("cardNo5"));
        tcmCardNo6.setCellValueFactory(new PropertyValueFactory<UserInfo, String>("cardNo6"));
        tcmCardNo7.setCellValueFactory(new PropertyValueFactory<UserInfo, String>("cardNo7"));
        tcmCardNo8.setCellValueFactory(new PropertyValueFactory<UserInfo, String>("cardNo8"));
        tcmCardNo9.setCellValueFactory(new PropertyValueFactory<UserInfo, String>("cardNo9"));
        tcmCardNo10.setCellValueFactory(new PropertyValueFactory<UserInfo, String>("cardNo10"));
        managerList = FXCollections.observableArrayList();
        managerTable.setItems(managerList);
    }

    private void createDeliveryTable() {
        tcmChecked.setCellFactory(tc -> new CheckBoxTableCell<UserInfo, Boolean>());
        tcmChecked.setCellValueFactory(new PropertyValueFactory<UserInfo, Boolean>("checked"));
        tcDelivery.setCellValueFactory(new PropertyValueFactory("userName"));
        tcdPhoneNo.setCellValueFactory(new PropertyValueFactory<UserInfo, String>("phoneNo"));
        tcdCardNo1.setCellValueFactory(new PropertyValueFactory<UserInfo, String>("cardNo1"));
        tcdCardNo2.setCellValueFactory(new PropertyValueFactory<UserInfo, String>("cardNo2"));
        tcdCardNo3.setCellValueFactory(new PropertyValueFactory<UserInfo, String>("cardNo3"));
        tcdCardNo4.setCellValueFactory(new PropertyValueFactory<UserInfo, String>("cardNo4"));
        tcdCardNo5.setCellValueFactory(new PropertyValueFactory<UserInfo, String>("cardNo5"));
        tcdCardNo6.setCellValueFactory(new PropertyValueFactory<UserInfo, String>("cardNo6"));
        tcdCardNo7.setCellValueFactory(new PropertyValueFactory<UserInfo, String>("cardNo7"));
        tcdCardNo8.setCellValueFactory(new PropertyValueFactory<UserInfo, String>("cardNo8"));
        tcdCardNo9.setCellValueFactory(new PropertyValueFactory<UserInfo, String>("cardNo9"));
        tcdCardNo10.setCellValueFactory(new PropertyValueFactory<UserInfo, String>("cardNo10"));
        deliveryList = FXCollections.observableArrayList();
        deliveryTable.setItems(deliveryList);
    }

    @FXML
    private void onBackAction(ActionEvent event) {
        app.goManager();
    }

    @FXML
    private void onAddUserAction(ActionEvent event) {
        Tab selectedTab = userContainer.getSelectionModel().getSelectedItem();
        UserEditController controller = loadUserEdit();
        controller.setUserClass(userContainer.getSelectionModel().getSelectedIndex());

    }

    @FXML
    private void onEditAction(ActionEvent event) {
        UserInfo user = getSelectedUserInfo();
        if (user == null) {
            SimpleDialog.showMessageDialog(app.getRootStage(), "请选择需要修改的用户.", "修改出错");
            return;
        }
        UserEditController controller = loadUserEdit();
        controller.setUserClass(userContainer.getSelectionModel().getSelectedIndex());
        controller.setUser(user);
    }

    private UserInfo getSelectedUserInfo() {
        Tab selectedTab = userContainer.getSelectionModel().getSelectedItem();
        UserInfo user;
        if (selectedTab == ownerTab) {
            user = ownerTable.getSelectionModel().getSelectedItem();
        } else if (selectedTab == deliveryTab) {
            user = deliveryTable.getSelectionModel().getSelectedItem();
        } else {
            user = managerTable.getSelectionModel().getSelectedItem();
        }
        return user;
    }

    private Integer getUserType() {
        Tab selectedTab = userContainer.getSelectionModel().getSelectedItem();

        if (selectedTab == ownerTab) {
            return UserInfo.OWNER;
        } else if (selectedTab == deliveryTab) {
            return UserInfo.DELIVERY;
        } else {
            return UserInfo.ADMIN;
        }
    }

    @FXML
    private void onDeleteAction(ActionEvent event) {
        List<UserInfo> users = getSelectedUsers();
        UserInfo userInfo = getSelectedUserInfo();
        if (userInfo == null) {
            SimpleDialog.showMessageDialog(app.getRootStage(), "请选择需要删除的用户.", "删除出错");
            return;
        }
        String message = "你确认要删除你选择的" + users.size() + "个用户吗？";
        SimpleDialog.Response response = SimpleDialog.showConfirmDialog(app.getRootStage(), message, "确认");
        if (response == SimpleDialog.Response.NO) return;

        for (int i = users.size() - 1; i >= 0; i--) {
            UserInfo user = users.get(i);
            if (user.getUserType() == UserInfo.FACTORY_USER && user.getUserName().equals("ADMIN")) {
                SimpleDialog.showAutoCloseError(app.getRootStage(), "超级用户ADMIN不能删除。");
                users.remove(i);
            }
            if (user.getUserId().equals(GlobalOption.currentUser.getUserId())) {
                SimpleDialog.showAutoCloseError(app.getRootStage(), "用户不能删除自己。");
                users.remove(i);
            }
        }
        if (users.size() == 0) return;

        if (users.size() == 1) {
            UserInfo user = users.get(0);

            UserService.delete(user.getUserId(), new RestfulResult() {
                @Override
                public void doResult(RfResultEvent event) {
                    onRefresh();
                    SimpleDialog.showAutoCloseInfo(app.getRootStage(), "删除成功。");
                }

                @Override
                public void doFault(RfFaultEvent event) {

                }
            });
        } else {
            String ids = getUserIds(users);
            UserService.deleteByIds(ids, new RestfulResult() {
                @Override
                public void doResult(RfResultEvent event) {
                    onRefresh();
                    SimpleDialog.showMessageDialog(app.getRootStage(), "删除成功。", "");
                }

                @Override
                public void doFault(RfFaultEvent event) {

                }
            });
        }
    }

    private String getUserIds(List<UserInfo> users) {
        String ids = "";
        for (int i = 0; i < users.size(); i++) {
            UserInfo user = users.get(i);
            ids += "," + user.getUserId();
        }
        if (ids.length() > 0) {
            ids = ids.substring(1);
        }
        return ids;
    }

    @FXML
    private void onSelectAllAction(ActionEvent event) {
        ObservableList<UserInfo> users = getUserLists();
        if (chkSelectAll.isSelected()) {
            selectAll(users, true);
            updateRecord.setDisable(true);
            setPassword.setDisable(true);
        } else {
            selectAll(users, false);
            updateRecord.setDisable(false);
            setPassword.setDisable(false);
        }
    }

    private List<UserInfo> getSelectedUsers() {
        List<UserInfo> selectedUsers = new ArrayList<>();

        ObservableList<UserInfo> users = getUserLists();
        for (int i = 0; i < users.size(); i++) {
            UserInfo user = users.get(i);
            if (user.isChecked()) {
                selectedUsers.add(user);
            }
        }

        return selectedUsers;
    }

    private ObservableList<UserInfo> getUserLists() {
        Tab selectedTab = userContainer.getSelectionModel().getSelectedItem();

        if (selectedTab == ownerTab) {
            return ownerList;
        } else if (selectedTab == deliveryTab) {
            return deliveryList;
        } else {
            return managerList;
        }
    }

    private void selectAll(ObservableList<UserInfo> users, Boolean checked) {
        for (int i = 0; i < users.size(); i++) {
            UserInfo user = (UserInfo) users.get(i);
            user.setChecked(checked);
        }
    }

    private UserEditController loadUserEdit() {
        try {
            FXMLLoader fxmlLoader;
            fxmlLoader = new FXMLLoader(getClass().getResource("UserEdit.fxml"));
            Parent root = fxmlLoader.load();
            UserEditController controller = (UserEditController) fxmlLoader.getController();

            getApp().getRootStage().getScene().setRoot(root);
            controller.setApp(app);
            return controller;

        } catch (Exception e) {
            SimpleDialog.showMessageDialog(getApp().getRootStage(), e.getMessage(), "错误");
            return null;
        }
    }

    public void selectTab(int index) {
        userContainer.getSelectionModel().select(index);
    }

    @FXML
    private void onSettingPasswordAction(ActionEvent event) {
        UserInfo user = getSelectedUserInfo();
        if (user == null) {
            SimpleDialog.showMessageDialog(app.getRootStage(),
                    "请选择一条需要设置密码的用户！", "");
            return;
        }
        ChangePasswordController controller = app.goChangePassword();
        controller.setUser(getSelectedUserInfo());
        GlobalOption.parents.push("userList");
        controller.setUserClass(userContainer.getSelectionModel().getSelectedIndex());
    }

    @FXML
    private void onImportFile(ActionEvent event) {
        File file = openFileChoose.showOpenDialog(app.getRootStage());
        if (file == null) return;

        ImportUserTask task = new ImportUserTask(file, getUserType());
        task.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                if (newValue == -1) {
                    onRefresh();
                }
            }
        });
        SimpleDialog.showDialog(app.getRootStage(), task, "", "");

    }

    @FXML
    private void onExportFile(ActionEvent event) {
        File file = saveFileChoose.showSaveDialog(app.getRootStage());
        if (file == null) return;

        ExportUserTask task = new ExportUserTask(file, getUserType());
        SimpleDialog.showDialog(app.getRootStage(), task, "", "");
    }


}
