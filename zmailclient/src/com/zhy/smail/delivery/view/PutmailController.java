package com.zhy.smail.delivery.view;

import com.zhy.smail.MainApp;
import com.zhy.smail.cabinet.entity.BoxInfo;
import com.zhy.smail.cabinet.entity.CabinetEntry;
import com.zhy.smail.cabinet.service.BoxService;
import com.zhy.smail.common.controller.RootController;
import com.zhy.smail.component.SimpleDialog;
import com.zhy.smail.config.GlobalOption;
import com.zhy.smail.delivery.service.SqlSuggestionProvider;
import com.zhy.smail.lcp.BoxEntry;
import com.zhy.smail.manager.entity.DeliveryLog;
import com.zhy.smail.manager.service.DeliveryLogService;
import com.zhy.smail.restful.RestfulResult;
import com.zhy.smail.restful.RfFaultEvent;
import com.zhy.smail.restful.RfResultEvent;
import com.zhy.smail.task.GetCabinetStatus;
import com.zhy.smail.user.entity.UserInfo;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by wenliz on 2017/2/22.
 */
public class PutmailController extends RootController implements Initializable {
    @FXML
    private Label lblTimer;
    @FXML
    private Region topLeft;
    @FXML
    private Region topRight;

    private List<BoxInfo> mailBoxes;

   @FXML
   private Label lblAvailableBox;

    @FXML
    private TextField txtRoomNo;
    @FXML
    private Button startDeliveryButton;

    private BoxInfo selectedBox;

    UserInfo user;


    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
        txtRoomNo.setText(user.getUserName());
        if(GlobalOption.deliverySameMail.getIntValue() == 1){
            DeliveryLogService.listByOwner(GlobalOption.currentCabinet.getCabinetId(), user.getUserId(),0, new RestfulResult() {
                @Override
                public void doResult(RfResultEvent event) {
                    if(event.getResult() == RfResultEvent.OK && event.getData()!= null){
                        List<DeliveryLog> logs = (List<DeliveryLog>)event.getData();
                        for(int i=0; i<logs.size(); i++){
                            DeliveryLog log = (DeliveryLog) logs.get(i);
                            if(log.getBoxInfo().getBoxType() == BoxInfo.BOX_TYPE_MAIL ||
                                    log.getBoxInfo().getBoxType() == BoxInfo.BOX_TYPE_SMALL){
                                //startDelivery(log.getBoxInfo(), log.getPickupUser());
                                selectedBox = log.getBoxInfo();
                                break;
                            }

                        }
                    }
                }

                @Override
                public void doFault(RfFaultEvent event) {

                }
            });
        }
    }

    BoxInfo box;

    public void setApp(MainApp app) {
        this.app = app;
        app.createTimeout(lblTimer);

        listAvailableBox();
    }


    public void checkBoxStatus(){
        BoxService.listByCabinetId(GlobalOption.currentCabinet.getCabinetId(), new RestfulResult() {
            @Override
            public void doResult(RfResultEvent event) {
                if(event.getResult()!=RfResultEvent.OK) return;

                List<BoxInfo> boxes = (List<BoxInfo>) event.getData();

                CabinetEntry cabinetEntry = new CabinetEntry();
                for(int i=0; i<boxes.size(); i++){
                    BoxInfo boxInfo = boxes.get(i);

                    cabinetEntry.addBox(boxInfo);
                }

                GetCabinetStatus task = new GetCabinetStatus(cabinetEntry);
                task.valueProperty().addListener(new ChangeListener<Integer>() {
                    @Override
                    public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                        if(newValue != 0) return;

                        boolean hasOpened = false;
                        String openNos="";
                        for(int i=0; i<boxes.size(); i++) {
                            BoxInfo boxInfo = boxes.get(i);
                            BoxEntry boxEntry = task.getCabinet().getBoxEntry(boxInfo.getControlCardId(), boxInfo.getControlSequence());
                            if (boxEntry == null) continue;

                            if(boxEntry.getStatus()==0 && boxInfo.isOpened()){
                                boxInfo.setOpened(false);
                                saveBox(boxInfo);
                            }
                            else if(boxEntry.getStatus() == 1 && !boxInfo.isOpened()){
                                boxInfo.setOpened(true);
                                saveBox(boxInfo);
                            }

                            if (!boxInfo.isLocked() && boxInfo.isOpened()) {
                                hasOpened = true;
                                openNos += "," +boxInfo.getSequence();
                            }
                        }
                        if(hasOpened){
                            openNos = openNos.substring(1);
                            SimpleDialog.showMessageDialog(app.getRootStage(), "请关上打开的箱门:"+openNos+"，否则不能投递.","");
                            startDeliveryButton.setDisable(true);
                            app.goDelivery();
                        }
                    }

                });
                SimpleDialog.showDialog(app.getRootStage(), task, "", "");



            }

            @Override
            public void doFault(RfFaultEvent event) {

            }
        });

    }

    public void initialize(URL location, ResourceBundle resources) {
        HBox.setHgrow(topLeft, Priority.ALWAYS);
        HBox.setHgrow(topRight, Priority.ALWAYS);

        mailBoxes = new ArrayList<>();

        user = null;
        box = null;
        selectedBox = null;
        createUserAutoCombBox(txtRoomNo);

    }

    private void listAvailableBox(){
        mailBoxes.clear();


        BoxService.listAvailableBox(GlobalOption.currentCabinet.getCabinetId(), new RestfulResult() {
            @Override
            public void doResult(RfResultEvent event) {
                if(event.getResult()!=RfResultEvent.OK) return;

                List<BoxInfo> boxes = (List<BoxInfo>) event.getData();
                for(int i=0; i<boxes.size(); i++){
                    BoxInfo box = boxes.get(i);
                    switch (box.getBoxType()){
                        case BoxInfo.BOX_TYPE_MAIL:
                            mailBoxes.add(box);
                            break;
                        case BoxInfo.BOX_TYPE_SMALL:
                            mailBoxes.add(box);
                            break;
                    }
                }
                setAvailableText();

            }

            @Override
            public void doFault(RfFaultEvent event) {

            }
        });
    }

    private void setAvailableText(){
        lblAvailableBox.setText(String.valueOf(mailBoxes.size()));
        if(mailBoxes.size() == 0){
            startDeliveryButton.setDisable(true);
            String nodesTypes = BoxInfo.BOX_TYPE_MAIL+","+BoxInfo.BOX_TYPE_SMALL;
            BoxService.getAnotherMaxAvailableCabinet(GlobalOption.currentCabinet.getCabinetId(), nodesTypes, new RestfulResult() {
                @Override
                public void doResult(RfResultEvent event) {
                    if(event.getResult()>0 ){
                        Integer count = Integer.valueOf(event.getData().toString());
                        if(count == 0){
                            SimpleDialog.showMessageDialog(app.getRootStage(), "全部信箱和小箱已满，不能投信.", "");
                        }
                        else {
                            String message = "本柜信箱和小箱已满," + event.getResult() + "号柜有" + event.getData().toString() + "个空箱,请到" + event.getResult() + "号柜投信.";
                            SimpleDialog.showMessageDialog(app.getRootStage(), message, "");

                        }
                    }
                }

                @Override
                public void doFault(RfFaultEvent event) {

                }
            });
        }
        else {
            startDeliveryButton.setDisable(false);
        }


    }



    protected void  changeUser(UserInfo user){
        setUser(user);
    }

    @FXML
    private void onStartDeliveryAction(ActionEvent event) {
        if(selectedBox == null){
            box =  mailBoxes.get(0);
        }
        else{
            box = selectedBox;
        }

        startDelivery(box, user);
    }

    public void startDelivery(BoxInfo box, UserInfo user){
        if(box == null){
            SimpleDialog.showMessageDialog(app.getRootStage(),"请选择空闲的箱门类别.", "");
            return;
        }
        if(user == null){
            SimpleDialog.showMessageDialog(app.getRootStage(), "请选择有效的房号或手机号码.","");
            return;
        }


        ConfirmDeliveryController controller = app.goConfirmDelivery();
        controller.setUser(user);
        controller.setBox(box);


        GlobalOption.parents.push("putmail");
    }

    @FXML
    public void onBackAction(ActionEvent event){
        app.goDelivery();
    }

    @FXML
    public void onChoiceUserAction(ActionEvent event){
        GlobalOption.parents.push("putmail");
        app.goUserChoice();
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
