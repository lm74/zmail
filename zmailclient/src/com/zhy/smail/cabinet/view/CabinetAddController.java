package com.zhy.smail.cabinet.view;

import com.zhy.smail.MainApp;
import com.zhy.smail.cabinet.entity.CabinetInfo;
import com.zhy.smail.cabinet.entity.CabinetProperty;
import com.zhy.smail.cabinet.service.CabinetService;
import com.zhy.smail.component.SimpleDialog;
import com.zhy.smail.component.keyboard.control.VkProperties;
import com.zhy.smail.restful.RestfulResult;
import com.zhy.smail.restful.RfFaultEvent;
import com.zhy.smail.restful.RfResultEvent;
import com.zhy.smail.user.view.UserListController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by wenliz on 2017/2/8.
 */
public class CabinetAddController implements Initializable{
    private MainApp app;
    private CabinetInfo cabinetInfo;
    private CabinetProperty cabinetProperty;

    public CabinetProperty getCabinetProperty() {
        return cabinetProperty;
    }

    public void setCabinetProperty(CabinetProperty cabinetProperty) {
        this.cabinetProperty = cabinetProperty;
    }

    private boolean isAdd;



    public boolean isAdd() {
        return isAdd;
    }

    public void setAdd(boolean add) {
        isAdd = add;
    }

    @FXML
    private Label lblTimer;

    public MainApp getApp() {
        return app;
    }

    public void setApp(MainApp app) {
        this.app = app;
        app.createTimeout(lblTimer);
    }

    @FXML
    private TextField txtCabinetNo;
    @FXML
    private Label lblBoxNumber;
    @FXML
    private Label lblTitle;
    @FXML
    private TextField txtController1;
    @FXML
    private TextField txtController2;
    @FXML
    private TextField txtController3;
    @FXML
    private TextField txtController4;
    @FXML
    private TextField txtController5;
    @FXML
    private TextField txtController6;
    @FXML
    private TextField txtController7;
    @FXML
    private TextField txtController8;
    @FXML
    private TextField txtController9;
    @FXML
    private TextField txtController10;

    public CabinetInfo getCabinetInfo() {
        return cabinetInfo;
    }

    public void setCabinetInfo(CabinetInfo cabinetInfo) {
        this.cabinetInfo = cabinetInfo;
        if(cabinetInfo == null) return;

        txtCabinetNo.setText(cabinetInfo.getCabinetNo().toString());
        lblBoxNumber.setText(cabinetInfo.getBoxNumber().toString());
        txtController1.setText(cabinetInfo.getController1BoxNumber().toString());
        txtController2.setText(cabinetInfo.getController2BoxNumber().toString());
        txtController3.setText(cabinetInfo.getController3BoxNumber().toString());
        txtController4.setText(cabinetInfo.getController4BoxNumber().toString());
        txtController5.setText(cabinetInfo.getController5BoxNumber().toString());
        txtController6.setText(cabinetInfo.getController6BoxNumber().toString());
        txtController7.setText(cabinetInfo.getController7BoxNumber().toString());
        txtController8.setText(cabinetInfo.getController8BoxNumber().toString());
        txtController9.setText(cabinetInfo.getController9BoxNumber().toString());
        txtController10.setText(cabinetInfo.getController10BoxNumber().toString());

        lblTitle.setText("修改箱柜");
    }

    public void initialize(URL location, ResourceBundle resources){
        isAdd = true;
        cabinetInfo = new CabinetInfo();

        txtCabinetNo.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_NUMERIC);
        initController(txtController1);
        initController(txtController2);
        initController(txtController3);
        initController(txtController4);
        initController(txtController5);
        initController(txtController6);
        initController(txtController7);
        initController(txtController8);
        initController(txtController9);
        initController(txtController10);

    }

    private  void initController(TextField txt) {
        txt.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_NUMERIC);
        txt.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(getInteger(txt)>50 || getInteger(txt)<0){
                    SimpleDialog.showMessageDialog(app.getRootStage(), "控制器箱门数量不能大于50或小于0", "错误");
                    return;
                }
                calculateBox();
            }
        });

    }

    private Integer getInteger(TextField txt){
        if(txt.getText()==null && txt.getLength()==0){
            return 0;
        }
        else{
            try {
                return Integer.parseInt(txt.getText());
            }
            catch (Exception e){
                return 0;
            }
        }
    }

    private void calculateBox(){
        int boxNumber = getInteger(txtController1) +   getInteger(txtController2) +
                getInteger(txtController3)+getInteger(txtController4)+
                getInteger(txtController5)+  getInteger(txtController6)+
                getInteger(txtController7)+  getInteger(txtController8)+
                getInteger(txtController9)+  getInteger(txtController10);

        cabinetInfo.setBoxNumber(boxNumber);
        lblBoxNumber.setText(String.valueOf(boxNumber));

    }

    @FXML
    private void onSaveAction(ActionEvent event){
        if(txtCabinetNo.getText().length()==0){
            SimpleDialog.showMessageDialog(app.getRootStage(), "箱柜编号不能为空，请输入唯一的编号。","错误");
            return;
        }
        cabinetInfo.setCabinetNo(txtCabinetNo.getText());
        cabinetInfo.setController1BoxNumber(getInteger(txtController1));
        cabinetInfo.setController2BoxNumber(getInteger(txtController2));
        cabinetInfo.setController3BoxNumber(getInteger(txtController3));
        cabinetInfo.setController4BoxNumber(getInteger(txtController4));
        cabinetInfo.setController5BoxNumber(getInteger(txtController5));
        cabinetInfo.setController6BoxNumber(getInteger(txtController6));
        cabinetInfo.setController7BoxNumber(getInteger(txtController7));
        cabinetInfo.setController8BoxNumber(getInteger(txtController8));
        cabinetInfo.setController9BoxNumber(getInteger(txtController9));
        cabinetInfo.setController10BoxNumber(getInteger(txtController10));
        calculateBox();
        CabinetService.save(cabinetInfo, new RestfulResult() {
            @Override
            public void doResult(RfResultEvent event) {
                if(event.getResult() == 0) {
                    app.goCabinetList();

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
    private void onBackAction(ActionEvent event){
        app.goCabinetList();
    }
}
