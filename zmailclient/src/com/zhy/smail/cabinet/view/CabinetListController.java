package com.zhy.smail.cabinet.view;

import com.zhy.smail.MainApp;
import com.zhy.smail.cabinet.entity.CabinetInfo;
import com.zhy.smail.cabinet.entity.CabinetProperty;
import com.zhy.smail.cabinet.service.CabinetService;
import com.zhy.smail.component.SimpleDialog;
import com.zhy.smail.component.TimeoutTimer;
import com.zhy.smail.config.GlobalOption;
import com.zhy.smail.restful.RestfulResult;
import com.zhy.smail.restful.RfFaultEvent;
import com.zhy.smail.restful.RfResultEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by wenliz on 2017/1/17.
 */
public class CabinetListController implements Initializable {
    @FXML
    private Label lblTimer;

    private MainApp app;
    @FXML
    private TableView<CabinetProperty> cabinetTable;
    private ObservableList cabinetList;

    @FXML
    private TableColumn<CabinetProperty, String> tcCabinetNo;
    @FXML
    private TableColumn<CabinetProperty, Integer> tcBoxNumber;
    @FXML
    private TableColumn<CabinetProperty, Integer> tcController1BoxNumber;
    @FXML
    private TableColumn<CabinetProperty, Integer> tcController2BoxNumber;
    @FXML
    private TableColumn<CabinetProperty, Integer> tcController3BoxNumber;
    @FXML
    private TableColumn<CabinetProperty, Integer> tcController4BoxNumber;
    @FXML
    private TableColumn<CabinetProperty, Integer> tcController5BoxNumber;
    @FXML
    private TableColumn<CabinetProperty, Integer> tcController6BoxNumber;
    @FXML
    private TableColumn<CabinetProperty, Integer> tcController7BoxNumber;
    @FXML
    private TableColumn<CabinetProperty, Integer> tcController8BoxNumber;
    @FXML
    private TableColumn<CabinetProperty, Integer> tcController9BoxNumber;
    @FXML
    private TableColumn<CabinetProperty, Integer> tcController10BoxNumber;


    public MainApp getApp() {
        return app;

    }

    public void setApp(MainApp app) {
        this.app = app;

        onRefresh();
        app.createTimeout(lblTimer);
    }

    private void onRefresh(){
        CabinetService.listAll(new RestfulResult() {
            @Override
            public void doResult(RfResultEvent event) {
                if(event.getData() != null){
                    cabinetList.clear();
                    List cabinets = (List)event.getData();
                    for(int i=0; i<cabinets.size(); i++){
                        CabinetInfo cabinetInfo = (CabinetInfo)cabinets.get(i);
                        cabinetList.add(CabinetProperty.valueOf(cabinetInfo));
                    }
                }
            }

            @Override
            public void doFault(RfFaultEvent event) {

            }
        });
    }

    public void initialize(URL location, ResourceBundle resources){
        tcCabinetNo.setCellValueFactory(cellData -> cellData.getValue().cabinetNoProperty());
        tcBoxNumber.setCellValueFactory(new PropertyValueFactory("boxNumber"));
        tcController1BoxNumber.setCellValueFactory(new PropertyValueFactory("controller1BoxNumber"));
        tcController2BoxNumber.setCellValueFactory(new PropertyValueFactory("controller2BoxNumber"));
        tcController3BoxNumber.setCellValueFactory(new PropertyValueFactory("controller3BoxNumber"));
        tcController4BoxNumber.setCellValueFactory(new PropertyValueFactory("controller4BoxNumber"));
        tcController5BoxNumber.setCellValueFactory(new PropertyValueFactory("controller5BoxNumber"));
        tcController6BoxNumber.setCellValueFactory(new PropertyValueFactory("controller6BoxNumber"));
        tcController7BoxNumber.setCellValueFactory(new PropertyValueFactory("controller7BoxNumber"));
        tcController8BoxNumber.setCellValueFactory(new PropertyValueFactory("controller8BoxNumber"));
        tcController9BoxNumber.setCellValueFactory(new PropertyValueFactory("controller9BoxNumber"));
        tcController10BoxNumber.setCellValueFactory(new PropertyValueFactory("controller10BoxNumber"));


        cabinetList = FXCollections.observableArrayList();
        cabinetTable.setItems(cabinetList);
    }

    @FXML
    private void onBackAction(ActionEvent event){
        app.goSetting();
    }

    @FXML
    private void onAddAction(ActionEvent event){
        try {
            FXMLLoader fxmlLoader;
            fxmlLoader = new FXMLLoader(getClass().getResource("CabinetAdd.fxml"));
            Parent root = fxmlLoader.load();
            CabinetAddController controller = (CabinetAddController) fxmlLoader.getController();

            getApp().getRootStage().getScene().setRoot(root);
            controller.setApp(app);
        }
        catch (Exception e){
            SimpleDialog.showMessageDialog(getApp().getRootStage(),e.getMessage(),"错误");
        }
    }

    @FXML
    private void onEditAction(ActionEvent event){
        try {
            CabinetProperty cabinet = cabinetTable.getSelectionModel().getSelectedItem();
            if(cabinet == null){
                SimpleDialog.showMessageDialog(app.getRootStage(), "请选择需要修改的箱柜.", "出错");
                return;
            }

            FXMLLoader fxmlLoader;
            fxmlLoader = new FXMLLoader(getClass().getResource("CabinetAdd.fxml"));
            Parent root = fxmlLoader.load();
            CabinetAddController controller = (CabinetAddController) fxmlLoader.getController();

            getApp().getRootStage().getScene().setRoot(root);
            controller.setApp(app);
            controller.setAdd(false);
            controller.setCabinetInfo(cabinet.getData());
        }
        catch (Exception e){
            SimpleDialog.showMessageDialog(getApp().getRootStage(),e.getMessage(),"错误");
        }
    }

    @FXML
    private void onDeleteAction(ActionEvent event){
        CabinetProperty cabinet = cabinetTable.getSelectionModel().getSelectedItem();
        if(cabinet == null){
            SimpleDialog.showMessageDialog(app.getRootStage(), "请选择需要删除的箱柜.", "删除出错");
            return;
        }
        String message = "你确认要删除你选择的箱柜(" + cabinet.getCabinetNo()+")吗？";
        SimpleDialog.Response  response = SimpleDialog.showConfirmDialog(app.getRootStage(),message ,"确认");
        if(response == SimpleDialog.Response.NO) return;

        CabinetService.delete(cabinet.getCabinetId(), new RestfulResult() {
            @Override
            public void doResult(RfResultEvent event) {
                onRefresh();
            }

            @Override
            public void doFault(RfFaultEvent event) {

            }
        });

    }


}
