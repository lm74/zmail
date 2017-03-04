package com.zhy.smail.user.view;

import com.sun.corba.se.spi.orbutil.fsm.Action;
import com.zhy.smail.MainApp;
import com.zhy.smail.component.SimpleDialog;
import com.zhy.smail.component.keyboard.control.VkProperties;
import com.zhy.smail.restful.RestfulResult;
import com.zhy.smail.restful.RfFaultEvent;
import com.zhy.smail.restful.RfResultEvent;
import com.zhy.smail.user.entity.UserInfo;
import com.zhy.smail.user.service.UserService;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by wenliz on 2017/2/14.
 */
public class UserEditController implements Initializable{
    private MainApp app;
    @FXML
    private Label lblTimer;
    @FXML
    private  Label lblTitle;
    @FXML
    private ToolBar topToolBar;
    @FXML
    private Region topLeft;
    @FXML
    private Region topRight;
    @FXML
    private TextField txtBuildingNo;
    @FXML
    private Label lblBuildingNo;
    @FXML
    private TextField txtUnitNo;
    @FXML
    private Label lblUnitNo;
    @FXML
    private TextField txtRoomNo;
    @FXML
    private Label lblRoomNo;
    @FXML
    private TextField txtPhoneNo;
    @FXML
    private TextField txtCard1No;
    @FXML
    private TextField txtCard2No;
    @FXML
    private TextField txtCard3No;
    @FXML
    private TextField txtCard4No;
    @FXML
    private TextField txtCard5No;
    @FXML
    private TextField txtCard6No;
    @FXML
    private TextField txtCard7No;
    @FXML
    private TextField txtCard8No;
    @FXML
    private TextField txtCard9No;
    @FXML
    private TextField txtCard10No;
    @FXML
    private Label lblUserType;
    @FXML
    private RadioButton rdoManager;
    @FXML
    private RadioButton rdoAdvanceManager;
    @FXML
    private RadioButton rdoFactory;
    @FXML
    private HBox userNameTool;

    private UserInfo user;
    private int userClass;
    private List<TextField> txtCards = new ArrayList<>();

    public int getUserClass() {
        return userClass;
    }

    public void setUserClass(int userClass) {

        this.userClass = userClass;
        switch (userClass){
            case 0:
                showOwner();
                break;
            case 1:
                hideOwner();
                showDelivery();
                break;
            case 2:
                hideOwner();
                showManager();
                break;
        }
    }
    private void showOwner(){
        txtBuildingNo.setPrefWidth(100);
        txtBuildingNo.requestFocus();
        txtUnitNo.setPrefWidth(100);
        txtRoomNo.setPrefWidth(150);

        userNameTool.getChildren().remove(rdoAdvanceManager);
        userNameTool.getChildren().remove(rdoManager);
        userNameTool.getChildren().remove(rdoFactory);
        userNameTool.getChildren().remove(lblUserType);
    }

    private void showDelivery(){
        lblUserType.setVisible(true);
        rdoManager.setVisible(true);
        rdoAdvanceManager.setVisible(true);

        rdoManager.setText("普通快递员");
        rdoAdvanceManager.setText("邮政快递员");

    }

    private void showManager(){
        lblUserType.setVisible(true);
        rdoManager.setVisible(true);
        rdoAdvanceManager.setVisible(true);
        rdoFactory.setVisible(true);
    }

    private void hideOwner(){
        lblBuildingNo.setText("用户名:");
        userNameTool.getChildren().remove(lblRoomNo);
        userNameTool.getChildren().remove(lblUnitNo);
        userNameTool.getChildren().remove(txtRoomNo);
        userNameTool.getChildren().remove(txtUnitNo);

        txtBuildingNo.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_TEXT);
        txtBuildingNo.setPrefWidth(275);
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
        lblTitle.setText("修改用户");
        showUser();
    }

    public MainApp getApp() {
        return app;
    }

    public void setApp(MainApp app) {
        this.app = app;
        app.createTimeout(lblTimer);
    }

    public void initialize(URL location, ResourceBundle resources){
        user = new UserInfo();
        user.setUserId(-1);
        user.setPassword("123456");
        HBox.setHgrow(topLeft, Priority.ALWAYS);
        HBox.setHgrow(topRight, Priority.ALWAYS);
        txtCards.add(txtCard1No);
        txtCards.add(txtCard2No);
        txtCards.add(txtCard3No);
        txtCards.add(txtCard4No);
        txtCards.add(txtCard5No);
        txtCards.add(txtCard6No);
        txtCards.add(txtCard7No);
        txtCards.add(txtCard8No);
        txtCards.add(txtCard9No);
        txtCards.add(txtCard10No);


        txtBuildingNo.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_NUMERIC);
        txtUnitNo.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_NUMERIC);
        txtRoomNo.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_NUMERIC);
        txtPhoneNo.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_NUMERIC);
        for(int i=0; i<txtCards.size(); i++){
            TextField txtCard = txtCards.get(i);
            txtCard.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_NUMERIC);
            txtCard.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    if(event.getCode() == KeyCode.ENTER){
                        String cardNo = txtCard.getText();
                        if(cardNo !=null && cardNo.length()>0 && (cardNo.substring(0,1).equals(";")||cardNo.substring(0,1).equals("；"))){
                            txtCard.setText(cardNo.substring(1));
                        }
                    }
                }
            });
        }

        lblUserType.setVisible(false);
        rdoManager.setVisible(false);
        rdoAdvanceManager.setVisible(false);
        rdoFactory.setVisible(false);
        txtBuildingNo.requestFocus();
    }

    @FXML
    private void onBackAction(ActionEvent event){
        UserListController controller =app.goUserList();
        controller.selectTab(userClass);
    }

    @FXML
    private void onSave(ActionEvent event){
        assignUser();
        UserService.save(user, new RestfulResult() {
            @Override
            public void doResult(RfResultEvent event) {
                if(event.getResult() == 0) {
                    UserListController controller = app.goUserList();
                    controller.selectTab(userClass);
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

    private void assignUser(){
        switch (userClass){
            case 0:
                user.setBuildingNo(txtBuildingNo.getText());
                user.setUnitNo(txtUnitNo.getText());
                user.setRoomNo(txtRoomNo.getText());
                user.setUserName(user.getBuildingNo()+user.getUnitNo()+user.getRoomNo());
                user.setUserType(UserInfo.OWNER);
                break;
            case 1:
                if(rdoManager.isSelected()) {
                    user.setUserType(UserInfo.DELIVERY);
                }
                else{
                    user.setUserType(UserInfo.MAILMAN);
                }
                user.setUserName(txtBuildingNo.getText());
                break;
            case 2:
                if(rdoManager.isSelected()){
                    user.setUserType(UserInfo.ADMIN);
                }
                else if(rdoAdvanceManager.isSelected()){
                    user.setUserType(UserInfo.ADVANCED_ADMIN);
                }
                else {
                    user.setUserType(UserInfo.FACTORY_USER);
                }
                user.setUserName(txtBuildingNo.getText());
                break;
        }
        user.setPhoneNo(txtPhoneNo.getText());
        user.setCardNo1(txtCard1No.getText());
        user.setCardNo2(txtCard2No.getText());
        user.setCardNo3(txtCard3No.getText());
        user.setCardNo4(txtCard4No.getText());
        user.setCardNo5(txtCard5No.getText());
        user.setCardNo6(txtCard6No.getText());
        user.setCardNo7(txtCard7No.getText());
        user.setCardNo8(txtCard8No.getText());
        user.setCardNo9(txtCard9No.getText());
        user.setCardNo10(txtCard10No.getText());
    }

    private void showUser(){
        txtBuildingNo.requestFocus();
        switch (userClass){
            case 0:
                txtBuildingNo.setText(user.getBuildingNo());
                txtUnitNo.setText(user.getUnitNo());
                txtRoomNo.setText(user.getRoomNo());
                break;
            case 1:
                txtBuildingNo.setText(user.getUserName());
                if(user.getUserType() == UserInfo.DELIVERY){
                    rdoManager.setSelected(true);
                }
                else{
                    rdoAdvanceManager.setSelected(true);
                }
                break;
            case 2:
                txtBuildingNo.setText(user.getUserName());
                if(user.getUserType() == UserInfo.ADMIN){
                    rdoManager.setSelected(true);
                }
                else if(user.getUserType() == UserInfo.ADVANCED_ADMIN){
                    rdoAdvanceManager.setSelected(true);
                }
                else{
                    rdoFactory.setSelected(true);
                }

                break;
        }
        txtPhoneNo.setText(user.getPhoneNo());
        txtCard1No.setText(user.getCardNo1());
        txtCard2No.setText(user.getCardNo2());
        txtCard3No.setText(user.getCardNo3());
        txtCard4No.setText(user.getCardNo4());
        txtCard5No.setText(user.getCardNo5());
        txtCard6No.setText(user.getCardNo6());
        txtCard7No.setText(user.getCardNo7());
        txtCard8No.setText(user.getCardNo8());
        txtCard9No.setText(user.getCardNo9());
        txtCard10No.setText(user.getCardNo10());
    }
}
