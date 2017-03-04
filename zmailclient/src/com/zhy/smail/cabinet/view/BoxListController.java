package com.zhy.smail.cabinet.view;

import com.zhy.smail.MainApp;
import com.zhy.smail.cabinet.entity.BoardEntry;
import com.zhy.smail.cabinet.entity.BoxInfo;
import com.zhy.smail.cabinet.entity.CabinetEntry;
import com.zhy.smail.cabinet.entity.CabinetInfo;
import com.zhy.smail.cabinet.service.BoxService;
import com.zhy.smail.cabinet.service.CabinetService;
import com.zhy.smail.component.SimpleDialog;
import com.zhy.smail.config.GlobalOption;
import com.zhy.smail.config.LocalConfig;
import com.zhy.smail.lcp.BoxEntry;
import com.zhy.smail.restful.RestfulResult;
import com.zhy.smail.restful.RfFaultEvent;
import com.zhy.smail.restful.RfResultEvent;
import com.zhy.smail.task.GetCabinetStatus;
import com.zhy.smail.task.OpenAllBoxTask;
import com.zhy.smail.task.OpenBoxTask;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by wenliz on 2017/1/17.
 */
public class BoxListController  implements Initializable {
    private MainApp app;
    private CabinetInfo currentCabinet;
    private  int currentCabinetIndex=0;
    private List<CabinetInfo> cabinets;
    private List<BoxInfo> boxes;
    private boolean checked;

    @FXML
    private CheckBox chkOpenBoxTest;

    public MainApp getApp() {
        return app;
    }

    public void setApp(MainApp app) {
        this.app = app;

        app.createTimeout(lblTimer);

        CabinetService.listAll(new RestfulResult() {
            @Override
            public void doResult(RfResultEvent event) {
                if(event.getResult() != RfResultEvent.OK) return;

                cabinetList.getItems().removeAll();
                cabinets = (List<CabinetInfo>)event.getData();

                for(int i=0; i<cabinets.size(); i++){
                    CabinetInfo cabinetInfo = (CabinetInfo)cabinets.get(i);
                    if(cabinetInfo.getCabinetNo().equals(LocalConfig.getInstance().getLocalCabinet())){
                        cabinetList.getItems().add("本柜");
                        currentCabinet = cabinetInfo;
                        currentCabinetIndex =i;
                    }
                    else {
                        cabinetList.getItems().add(cabinetInfo.getCabinetNo());
                    }
                }
                cabinetList.getSelectionModel().select(currentCabinetIndex);
                cabinetList.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                        currentCabinetIndex = newValue.intValue();
                        currentCabinet = cabinets.get(currentCabinetIndex);
                        onRefreshBoxes();
                    }
                });
                onRefreshBoxes();
            }

            @Override
            public void doFault(RfFaultEvent event) {

            }
        });
    }
    @FXML
    private Label lblTimer;

    @FXML
    private FlowPane boxesFlow;
    @FXML
    private ChoiceBox<String> cabinetList;


    public void initialize(URL location, ResourceBundle resources){
        boxesFlow.getChildren().remove(0, boxesFlow.getChildren().size());
        boxesFlow.setHgap(10);
        boxesFlow.setVgap(10);
        checked = false;
    }

    @FXML
    private  void onRefreshAction(ActionEvent event){
        checked = false;
        onRefreshBoxes();
    }

    private void onRefreshBoxes(){
        currentCabinet = cabinets.get(currentCabinetIndex);
        BoxService.listByCabinetId(currentCabinet.getCabinetId(), new RestfulResult() {
            @Override
            public void doResult(RfResultEvent event) {
                if(event.getResult() != RfResultEvent.OK) return;

                boxes = (List<BoxInfo>)event.getData();
                CabinetEntry cabinetEntry = new CabinetEntry();
                boxesFlow.getChildren().remove(0, boxesFlow.getChildren().size());
                for(int i=0; i<boxes.size(); i++){
                    BoxInfo boxInfo = boxes.get(i);
                    createButton(boxInfo);

                    cabinetEntry.addBox(boxInfo);
                }

                if((currentCabinet.getCabinetId().equals( GlobalOption.currentCabinet.getCabinetId()))  && (!checked)){
                    checked = true;
                    GetCabinetStatus  task = new GetCabinetStatus(cabinetEntry);
                    task.valueProperty().addListener(new ChangeListener<Integer>() {
                        @Override
                        public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                            if(newValue != 0) return;

                            boolean changed = false;
                            for(int i=0; i<boxes.size(); i++){
                                BoxInfo boxInfo = boxes.get(i);
                                BoxEntry boxEntry = task.getCabinet().getBoxEntry(boxInfo.getControlCardId(), boxInfo.getControlSequence());
                                if(boxEntry== null ) continue;

                                if(boxInfo.isOpened() && boxEntry.getStatus()  == 0){
                                    boxInfo.setOpened(false);
                                    saveBox(boxInfo);
                                    changed = true;
                                }
                                else if(!boxInfo.isOpened() && boxEntry.getStatus() == 1){
                                    boxInfo.setOpened(true);
                                    saveBox(boxInfo);
                                    changed = true;
                                }
                            }
                            onRefreshBoxes();
                        }

                    });
                    SimpleDialog.showDialog(app.getRootStage(), task, "", "");

                }

            }

            private void createButton(BoxInfo boxInfo) {
                Button button = boxInfo.createButton();
                boxesFlow.getChildren().add(button);

                button.addEventHandler(ActionEvent.ACTION, (ActionEvent actionEvent)-> {
                    if(chkOpenBoxTest.isSelected()){
                        onOpenBoxAction(boxInfo);
                    }
                    else {
                        try {
                            FXMLLoader fxmlLoader;
                            fxmlLoader = new FXMLLoader(getClass().getResource("BoxEdit.fxml"));
                            Parent root = fxmlLoader.load();
                            BoxEditController controller = (BoxEditController) fxmlLoader.getController();

                            getApp().getRootStage().getScene().setRoot(root);
                            controller.setBoxInfo(boxInfo);
                            controller.setApp(app);

                        } catch (Exception e) {
                            SimpleDialog.showMessageDialog(getApp().getRootStage(), e.getMessage(), "错误");
                        }
                    }
                });
            }

            @Override
            public void doFault(RfFaultEvent event) {

            }
        });
    }



    @FXML
    private void onBackAction(ActionEvent event){
        app.goManager();
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

    private void onOpenBoxAction(BoxInfo boxInfo){
        OpenBoxTask task = new OpenBoxTask(boxInfo, "正在打开箱门("+boxInfo.getSequence()+")...");
        task.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                if(newValue!=null && newValue == 0){
                    saveBox(boxInfo);

                }
            }
        });
        SimpleDialog.showDialog(app.getRootStage(), task, "", "");
    }



    @FXML
    private void onOpenAllBox(ActionEvent event){
        CabinetEntry cabinetEntry = getCabinetEntry();

        OpenAllBoxTask task = new OpenAllBoxTask(cabinetEntry);
        task.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                if(newValue != 0) return;


                for(int i=0; i<boxes.size(); i++){
                    BoxInfo boxInfo = boxes.get(i);
                        boxInfo.setOpened(true);
                        saveBox(boxInfo);

                }
                onRefreshBoxes();
            }

        });
        SimpleDialog.showDialog(app.getRootStage(), task, "", "");
    }

    private CabinetEntry getCabinetEntry() {
        CabinetEntry cabinetEntry = new CabinetEntry();
        for(int i=0; i<boxes.size(); i++){
            BoxInfo boxInfo = boxes.get(i);
            if(boxInfo.isLocked()) continue;

            cabinetEntry.addBox(boxInfo);
        }
        return cabinetEntry;
    }

    @FXML
    private void onClearAllBox(ActionEvent event){
        CabinetEntry cabinetEntry = getCabinetEntry();

        OpenAllBoxTask task = new OpenAllBoxTask(cabinetEntry);
        task.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                if(newValue != 0) return;


                for(int i=0; i<boxes.size(); i++){
                    BoxInfo boxInfo = boxes.get(i);
                    boxInfo.setOpened(true);
                    boxInfo.setUsed(false);
                    saveBox(boxInfo);

                }
                onRefreshBoxes();
            }

        });
        SimpleDialog.showDialog(app.getRootStage(), task, "", "");
    }

    @FXML
    private void onOpenBoxTestAction(ActionEvent event){

    }

}
