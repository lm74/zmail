package com.zhy.smail.delivery.view;

import com.zhy.smail.MainApp;
import com.zhy.smail.cabinet.entity.BoxInfo;
import com.zhy.smail.cabinet.entity.CabinetEntry;
import com.zhy.smail.cabinet.service.BoxService;
import com.zhy.smail.common.controller.RootController;
import com.zhy.smail.component.SimpleDialog;
import com.zhy.smail.config.GlobalOption;
import com.zhy.smail.lcp.BoxEntry;
import com.zhy.smail.restful.RestfulResult;
import com.zhy.smail.restful.RfFaultEvent;
import com.zhy.smail.restful.RfResultEvent;
import com.zhy.smail.task.GetCabinetStatus;
import com.zhy.smail.user.entity.UserInfo;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Administrator on 2017/3/11.
 */
public class CommonDeliveryController extends RootController implements Initializable {
    @FXML
    private Label lblTimer;
    @FXML
    private Region topLeft;
    @FXML
    private Region topRight;
    @FXML
    private Button mailButton;
    @FXML
    private Button packetButton;
    @FXML
    private Button tomailButton;

    private List<BoxInfo> smallBoxes;
    private List<BoxInfo> middleBoxes;
    private List<BoxInfo> largeBoxes;

    @FXML
    private Button smallButton;
    @FXML
    private Button middleButton;
    @FXML
    private Button largeButton;
    @FXML
    private Label lblMessage;
    @FXML
    private Label lblLine1;

    private BoxInfo box;


    public void setApp(MainApp app) {
        this.app = app;
        app.createTimeout(lblTimer);
        listAvailableBox();

    }

    @FXML
    private void onRefreshBox(ActionEvent event) {
        checkBoxStatus();
    }

    public void checkBoxStatus() {
        BoxService.listByCabinetId(GlobalOption.currentCabinet.getCabinetId(), new RestfulResult() {
            @Override
            public void doResult(RfResultEvent event) {
                if (event.getResult() != RfResultEvent.OK) return;

                List<BoxInfo> boxes = (List<BoxInfo>) event.getData();

                CabinetEntry cabinetEntry = new CabinetEntry();
                for (int i = 0; i < boxes.size(); i++) {
                    BoxInfo boxInfo = boxes.get(i);

                    cabinetEntry.addBox(boxInfo);
                }

                GetCabinetStatus task = new GetCabinetStatus(cabinetEntry);
                task.valueProperty().addListener(new ChangeListener<Integer>() {
                    @Override
                    public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                        if (newValue != 0) return;

                        boolean hasOpened = false;
                        String openNos = "";
                        for (int i = 0; i < boxes.size(); i++) {
                            BoxInfo boxInfo = boxes.get(i);
                            BoxEntry boxEntry = task.getCabinet().getBoxEntry(boxInfo.getControlCardId(), boxInfo.getControlSequence());
                            if (boxEntry == null) continue;

                            if (boxEntry.getStatus() == 0 && boxInfo.isOpened()) {
                                boxInfo.setOpened(false);
                                saveBox(boxInfo);
                            } else if (boxEntry.getStatus() == 1 && !boxInfo.isOpened()) {
                                boxInfo.setOpened(true);
                                saveBox(boxInfo);
                            }

                            if (!boxInfo.isLocked() && boxInfo.isOpened()) {
                                hasOpened = true;
                                openNos += "," + boxInfo.getSequence();
                            }
                        }
                        if (hasOpened) {
                            openNos = openNos.substring(1);
                            lblMessage.setVisible(true);
                            lblLine1.setVisible(true);
                            lblLine1.setText("请关上打开的以下箱门，否则不能投递！关闭后请点击‘刷新状态’");
                            lblMessage.setText(openNos);
                            smallButton.setDisable(true);
                            middleButton.setDisable(true);
                            largeButton.setDisable(true);

                        } else {
                            lblMessage.setVisible(false);
                            lblLine1.setVisible(false);
                            smallButton.setDisable(false);
                            middleButton.setDisable(false);
                            largeButton.setDisable(false);
                            if (smallBoxes.size() == 0) {
                                smallButton.setDisable(true);
                            } else {
                                smallButton.setDisable(false);
                            }
                            if (middleBoxes.size() == 0) {
                                middleButton.setDisable(true);
                            } else {
                                middleButton.setDisable(false);
                            }
                            if (largeBoxes.size() == 0) {
                                largeButton.setDisable(true);
                            } else {
                                largeButton.setDisable(false);
                            }
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

    @FXML
    public void onSmallAction(ActionEvent event) {
        if (smallBoxes.size() > 0) {
            box = smallBoxes.get(0);
        }
        goPutdown();

    }

    @FXML
    public void onMiddleAction(ActionEvent event) {
        if (middleBoxes.size() > 0) {
            box = middleBoxes.get(0);
        }
        goPutdown();

    }

    @FXML
    public void onLargeAction(ActionEvent event) {
        if (largeBoxes.size() > 0) {
            box = largeBoxes.get(0);
        }
        goPutdown();

    }

    private void goPutdown() {
        if (box == null) {
            SimpleDialog.showMessageDialog(app.getRootStage(), "请选择空闲的箱门类别.", "");
            return;
        }

        SelectRoomController controller = app.goSelectRoom();
        controller.setBox(box);
    }

    public void initialize(URL location, ResourceBundle resources) {
        lblMessage.setText("");
        lblLine1.setText("");
        HBox.setHgrow(topLeft, Priority.ALWAYS);
        HBox.setHgrow(topRight, Priority.ALWAYS);

        UserInfo user = GlobalOption.currentUser;

        smallBoxes = new ArrayList<>();
        middleBoxes = new ArrayList<>();
        largeBoxes = new ArrayList<>();
        box = null;

    }

    private void listAvailableBox() {

        smallBoxes.clear();
        middleBoxes.clear();
        largeBoxes.clear();
        BoxService.listAvailableBox(GlobalOption.currentCabinet.getCabinetId(), new RestfulResult() {
            @Override
            public void doResult(RfResultEvent event) {
                if (event.getResult() != RfResultEvent.OK) return;

                List<BoxInfo> boxes = (List<BoxInfo>) event.getData();
                for (int i = 0; i < boxes.size(); i++) {
                    BoxInfo box = boxes.get(i);
                    switch (box.getBoxType()) {
                        case BoxInfo.BOX_TYPE_SMALL:
                            smallBoxes.add(box);
                            break;
                        case BoxInfo.BOX_TYPE_MIDDLE:
                            middleBoxes.add(box);
                            break;
                        case BoxInfo.BOX_TYPE_LARGE:
                            largeBoxes.add(box);
                            break;
                    }
                }
                setButtonsText();
                checkBoxStatus();


            }

            @Override
            public void doFault(RfFaultEvent event) {

            }
        });
    }

    private void setButtonsText() {

        setButtonText(smallButton, "小包箱", smallBoxes);
        setButtonText(middleButton, "中包箱", middleBoxes);
        setButtonText(largeButton, "大包箱", largeBoxes);
        int count = smallBoxes.size() + middleBoxes.size() + largeBoxes.size();
        if (count > 0) return;

        String nodesTypes = BoxInfo.BOX_TYPE_SMALL + "," + BoxInfo.BOX_TYPE_MIDDLE + "," + BoxInfo.BOX_TYPE_LARGE;
        BoxService.getAnotherMaxAvailableCabinet(GlobalOption.currentCabinet.getCabinetId(), nodesTypes, new RestfulResult() {
            @Override
            public void doResult(RfResultEvent event) {
                if (event.getResult() > 0) {
                    Integer count = Integer.valueOf(event.getData().toString());
                    if (count == 0) {
                        lblMessage.setText("全部箱门已满，不能投件.");
                    } else {
                        String message = "本柜箱门已满," + event.getResult() + "号柜有" + event.getData().toString() + "个空箱,请到" + event.getResult() + "号柜投件.";
                        lblMessage.setText(message);
                        //SimpleDialog.showMessageDialog(app.getRootStage(), message, "");

                    }
                }
            }

            @Override
            public void doFault(RfFaultEvent event) {

            }
        });
    }

    private void setButtonText(Button button, String title, List<BoxInfo> boxes) {
        button.setText(title + "(" + boxes.size() + ")");
        if (boxes.size() == 0) {
            button.setDisable(true);
        } else {
            button.setDisable(false);
        }
    }

    @FXML
    public void onBackAction(ActionEvent event) {
        app.goHome();
    }


    @FXML
    public void onRecordAction(ActionEvent actionEvent) throws IOException {
        GlobalOption.parents.push("commonDelivery");
        FXMLLoader fxmlLoader;
        fxmlLoader = new FXMLLoader(getClass().getResource("deliveryLog.fxml"));
        Parent root = fxmlLoader.load();
        DeliveryLogController controller = fxmlLoader.getController();
        controller.setApp(app);
        app.getRootScene().setRoot(root);
    }

    @FXML
    public void onUserView(ActionEvent event) {
        app.goUserView();
        GlobalOption.parents.push("commonDelivery");
    }

    private void saveBox(BoxInfo boxInfo) {
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