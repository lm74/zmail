package com.zhy.smail.delivery.view;

import com.zhy.smail.MainApp;
import com.zhy.smail.cabinet.entity.BoxInfo;
import com.zhy.smail.cabinet.entity.CabinetEntry;
import com.zhy.smail.cabinet.entity.CabinetInfo;
import com.zhy.smail.cabinet.service.BoxService;
import com.zhy.smail.cabinet.service.CabinetService;
import com.zhy.smail.cabinet.view.BoxEditController;
import com.zhy.smail.common.controller.RootController;
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
 * Created by wenliz on 2017/2/28.
 */
public class OccupyBoxController extends RootController implements Initializable {
    private List<BoxInfo> boxes;
    private boolean checked;
    @FXML
    private Label lblTimer;
    @FXML
    private FlowPane boxesFlow;

    public void setApp(MainApp app) {
        this.app = app;
        app.createTimeout(lblTimer);
        onRefreshBoxes();

    }


    public void initialize(URL location, ResourceBundle resources){
        boxesFlow.getChildren().remove(0, boxesFlow.getChildren().size());
        boxesFlow.setHgap(10);
        boxesFlow.setVgap(10);
        checked = false;
    }

    private void onRefreshBoxes(){
        CabinetInfo currentCabinet = GlobalOption.currentCabinet;
        BoxService.listByCabinetId(currentCabinet.getCabinetId(), new RestfulResult() {
            @Override
            public void doResult(RfResultEvent event) {
                if(event.getResult() != RfResultEvent.OK) return;

                boxes = (List<BoxInfo>)event.getData();
                boxesFlow.getChildren().remove(0, boxesFlow.getChildren().size());
                for(int i=0; i<boxes.size(); i++){
                    BoxInfo boxInfo = boxes.get(i);
                    if(boxInfo.getBoxType().equals(BoxInfo.BOX_TYPE_MAIL)||boxInfo.getBoxType().equals(BoxInfo.BOX_TYPE_SMALL)) {
                        createButton(boxInfo);
                    }
                }
            }

            private void createButton(BoxInfo boxInfo) {
                Button button = boxInfo.createButton(true);
                boxesFlow.getChildren().add(button);

                button.addEventHandler(ActionEvent.ACTION, (ActionEvent actionEvent)-> {
                    if(boxInfo.getBoxType() == BoxInfo.BOX_TYPE_SMALL){
                        boxInfo.setBoxType(BoxInfo.BOX_TYPE_MAIL);

                    }
                    else{
                        boxInfo.setBoxType(BoxInfo.BOX_TYPE_SMALL);
                    }
                    saveBox(boxInfo);
                    onRefreshBoxes();
                });
            }

            @Override
            public void doFault(RfFaultEvent event) {

            }
        });
    }



    @FXML
    private void onBackAction(ActionEvent event){
        app.goDelivery();
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



}
