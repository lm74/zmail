package com.zhy.smail.user.view;

import com.zhy.smail.MainApp;
import com.zhy.smail.common.controller.RootController;
import com.zhy.smail.component.SimpleDialog;
import com.zhy.smail.component.keyboard.control.VkProperties;
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
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by wenliz on 2017/2/21.
 */
public class UserViewController extends RootController implements Initializable {
    @FXML
    private Label lblTimer;
    @FXML
    private ToolBar topToolBar;
    @FXML
    private Region topLeft;
    @FXML
    private Region topRight;
    @FXML
    private Label lblUserNameTitle;
    @FXML
    private Label lblUserName;
    @FXML
    private TextField txtPhoneNo;

    private UserInfo user;
    private Integer parentType;

    public Integer getParentType() {
        return parentType;
    }

    public void setParentType(Integer parentType) {

        this.parentType = parentType;
    }

    public void setApp(MainApp app) {
        this.app = app;
        app.createTimeout(lblTimer);
    }

    public void initialize(URL location, ResourceBundle resources) {
        user = GlobalOption.currentUser;
        if(user.getUserType() == UserInfo.OWNER){
            lblUserNameTitle.setText("房号:");
        }
        lblUserName.setText(user.getUserName());
        txtPhoneNo.setText(user.getPhoneNo());
        txtPhoneNo.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_NUMERIC);
        HBox.setHgrow(topLeft, Priority.ALWAYS);
        HBox.setHgrow(topRight, Priority.ALWAYS);
    }

    @FXML
    private void onBackAction(ActionEvent event){
        String parent = GlobalOption.parents.pop();
        if(parent == null || parent.equals("manager")){
            app.goManager();
        }
        else if(parent.equals("delivery")){
            app.goDelivery();
        }
        else if(parent.equals("commonDelivery")){
            app.goCommonDelivery();
        }
        else{
            app.goOwner();
        }
    }

    @FXML
    private void onSave(ActionEvent event){
        user.setPhoneNo(txtPhoneNo.getText());
        UserService.save(user, new RestfulResult() {
            @Override
            public void doResult(RfResultEvent event) {
                if(event.getResult() == 0) {
                    SimpleDialog.showAutoCloseInfo(app.getRootStage(),"保存成功。");
                }
                else{
                    SimpleDialog.showMessageDialog(app.getRootStage(), event.getMessage(), "保存出错");
                }
            }

            @Override
            public void doFault(RfFaultEvent event) {
                SimpleDialog.showMessageDialog(app.getRootStage(), event.getMessage(), "保存出错");
            }
        });
    }

    @FXML
    private void onChangePassword(ActionEvent event){

        app.goChangePassword();
        GlobalOption.parents.push("userView");
    }
}
