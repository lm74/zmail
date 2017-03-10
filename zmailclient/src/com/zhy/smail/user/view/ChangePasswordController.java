package com.zhy.smail.user.view;

import com.zhy.smail.MainApp;
import com.zhy.smail.common.utils.KeySecurity;
import com.zhy.smail.component.SimpleDialog;
import com.zhy.smail.config.GlobalOption;
import com.zhy.smail.restful.RestfulResult;
import com.zhy.smail.restful.RfFaultEvent;
import com.zhy.smail.restful.RfResultEvent;
import com.zhy.smail.user.entity.UserInfo;
import com.zhy.smail.user.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by wenliz on 2017/2/18.
 */
public class ChangePasswordController  implements Initializable {
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
    private PasswordField txtOldPassword;
    @FXML
    private PasswordField txtNewPassword;
    @FXML
    private PasswordField txtConfigPassword;
    @FXML
    private Label lblOldPassword;

    private Integer parentType;
    private Integer userClass;

    public Integer getUserClass() {
        return userClass;
    }

    public void setUserClass(Integer userClass) {
        this.userClass = userClass;

        txtOldPassword.setDisable(true);
        txtOldPassword.setText(user.getPassword());

    }

    public Integer getParentType() {
        return parentType;
    }

    public void setParentType(Integer parentType) {
        this.parentType = parentType;
        if(parentType == 0 ){
            txtOldPassword.setDisable(true);
            txtOldPassword.setText(user.getPassword());
        }
    }

    private UserInfo user;

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public MainApp getApp() {
        return app;
    }

    public void setApp(MainApp app) {
        this.app = app;
        app.createTimeout(lblTimer);
    }

    public void initialize(URL location, ResourceBundle resources) {
        user = GlobalOption.currentUser;
        HBox.setHgrow(topLeft, Priority.ALWAYS);
        HBox.setHgrow(topRight, Priority.ALWAYS);
    }

    @FXML
    private void onBackAction(ActionEvent event){
        String parent = GlobalOption.parents.pop();
        if(parent == null || parent.equals("userList")){
            UserListController controller =app.goUserList();
            controller.selectTab(userClass);
        }
        else if(parent.equals("userView")){
            app.goUserView();
        }
    }

    @FXML
    private void onSave(ActionEvent event){
        if(txtOldPassword.getText()==null || txtOldPassword.getText().length() == 0){
            SimpleDialog.showMessageDialog(app.getRootStage(), "旧密码不能为空.","错误");
            txtOldPassword.requestFocus();
            return;
        }
        if(txtNewPassword.getText().length() == 0){
            SimpleDialog.showMessageDialog(app.getRootStage(), "新密码不能为空","错误");
            txtNewPassword.requestFocus();
            return;
        }
        if(txtConfigPassword.getText().length() == 0){
            SimpleDialog.showMessageDialog(app.getRootStage(), "确认密码不能为空","错误");
            txtConfigPassword.requestFocus();
            return;
        }
        if(!txtNewPassword.getText().equals(txtConfigPassword.getText())){
            SimpleDialog.showMessageDialog(app.getRootStage(), "新密码和确认密码不一不致.","错误");
            txtNewPassword.requestFocus();
            return;
        }
        String oldPassword="";
        if(txtOldPassword.isDisable()){
            oldPassword = user.getPassword();
        }
        else {
            oldPassword = KeySecurity.encrypt(txtOldPassword.getText());
        }
        String newPasword = KeySecurity.encrypt(txtNewPassword.getText());
        UserService.changePassword(user.getUserId(), oldPassword, newPasword, new RestfulResult() {
            @Override
            public void doResult(RfResultEvent event) {
                if(event.getResult() == 0){
                    SimpleDialog.showAutoCloseInfo(app.getRootStage(), "密码修改成功。");
                    onBackAction(null);
                }
                else if(event.getResult() == -2){
                    SimpleDialog.showMessageDialog(app.getRootStage(), "旧密码不正确.","错误");
                    txtOldPassword.requestFocus();
                    return;
                }
                else{
                    SimpleDialog.showMessageDialog(app.getRootStage(), "用户信息不全，修改失败.","错误");
                    txtOldPassword.requestFocus();
                    return;
                }
            }

            @Override
            public void doFault(RfFaultEvent event) {

            }
        });
    }
}
