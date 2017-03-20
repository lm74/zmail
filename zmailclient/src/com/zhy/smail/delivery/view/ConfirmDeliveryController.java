package com.zhy.smail.delivery.view;

import com.zhy.smail.MainApp;
import com.zhy.smail.cabinet.entity.BoxInfo;
import com.zhy.smail.common.controller.RootController;
import com.zhy.smail.common.utils.SystemUtil;
import com.zhy.smail.component.SimpleDialog;
import com.zhy.smail.component.music.Speaker;
import com.zhy.smail.config.GlobalOption;
import com.zhy.smail.lcp.LcProtocol;
import com.zhy.smail.lcp.LcResult;
import com.zhy.smail.lcp.command.LcCommand;
import com.zhy.smail.manager.service.DeliveryLogService;
import com.zhy.smail.manager.service.OpeningLogService;
import com.zhy.smail.restful.DefaultRestfulResult;
import com.zhy.smail.restful.RestfulResult;
import com.zhy.smail.restful.RfFaultEvent;
import com.zhy.smail.restful.RfResultEvent;
import com.zhy.smail.task.ResponseManager;
import com.zhy.smail.task.SendManager;
import com.zhy.smail.user.entity.UserInfo;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

/**
 * Created by wenliz on 2017/2/22.
 */
public class ConfirmDeliveryController extends RootController implements Initializable {
    @FXML
    private Label lblTimer;
    @FXML
    private Region topLeft;
    @FXML
    private Region topRight;
    @FXML
    private Button confirmButton;
    @FXML
    private Label lblConfirmMessage;
    @FXML
    private Label lblLine1;

    private UserInfo user;
    private BoxInfo box;

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public BoxInfo getBox() {
        return box;
    }

    public void setBox(BoxInfo box) {
        this.box = box;

        openBox();
    }

    public void setApp(MainApp app) {
        this.app = app;
        app.createTimeout(lblTimer);
    }

    public void initialize(URL location, ResourceBundle resources) {
        confirmButton.setDisable(true);
        lblConfirmMessage.setVisible(false);

        HBox.setHgrow(topLeft, Priority.ALWAYS);
        HBox.setHgrow(topRight, Priority.ALWAYS);
    }

    private void openBox(){
        int[] boxList = new int[]{box.getControlSequence()};

        SimpleDialog.showDialog(app.getRootStage(), new Task() {
            @Override
            protected Object call() throws Exception {
                updateMessage("正在开"+box.getSequence()+"号箱门...");
                if(!SystemUtil.canUse()){
                    updateMessage("开箱失败，请联系厂家(9999)。");
                    updateValue(-1);
                    return -1;
                }
                ResponseManager.response.clear();
                try {
                    LcProtocol protocol = new LcProtocol();
                    byte[] packet = protocol.pack(LcCommand.OPEN_BOX, box.getControlCardId(), boxList);
                    SendManager.gateway.sendMessage(packet);

                    LcResult result = ResponseManager.response.poll(ResponseManager.WAIT_SECONDS, TimeUnit.SECONDS);
                    if (result == null) {
                        updateMessage("开"+box.getSequence()+"号箱门开启失败，请重试或联系管理员。");
                        updateValue(-1);
                    } else {
                        if (result.getErrorNo() == LcResult.SUCCESS) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    confirmButton.setDisable(false);
                                    lblConfirmMessage.setVisible(true);
                                    lblLine1.setText("投递对象:" + user.getBuildingNo()+"栋" + user.getUnitNo()+"单元"+user.getFloorNo()+user.getRoomNo()+"号房");
                                    lblConfirmMessage.setText(box.getSequence()+"号箱门已开，请放入物品后，点击确认投递。");
                                    OpeningLogService.save(GlobalOption.currentUser.getUserId(),  box.getBoxId(), "开箱成功", new DefaultRestfulResult());
                                    Speaker.delivery();
                                }
                            });

                            updateValue(0);
                            return null;
                        } else {
                            updateMessage("开"+box.getSequence()+"号箱门开启失败，请重试或联系管理员。");
                            updateValue(-1);
                        }
                    }
                    return null;
                }
               catch (InterruptedException e){
                    updateMessage("设备没有响应，请确认设备是否运行正常.");
                }
                catch (IOException e){
                    updateMessage("发送数据包失败:" + e.getMessage());
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                updateValue(-1);
                OpeningLogService.save(GlobalOption.currentUser.getUserId(),  box.getBoxId(), "开箱失败", new DefaultRestfulResult());
                return null;
            }
        }, "正在开箱...", "开箱");
    }

    @FXML
    public void onBackAction(ActionEvent event){
        String parent = GlobalOption.parents.pop();

        if(parent!=null) {
            if (parent.equals("selectRoom")) {
                app.goCommonDelivery();
            } else if (parent.equals("putmail")) {
                app.goPutmail();
            } else {
                app.goPutdown();
            }
        }

    }

    @FXML
    public void onConfirmAction(ActionEvent event){

        String parent = GlobalOption.parents.getFirst();
        Integer deliveryType = 0;
        if(parent.equals("putdown") || parent.equals("selectRoom")){
            deliveryType = 1;
        }
        Integer deliveryMan = GlobalOption.currentUser.getUserId();
        DeliveryLogService.putdown(deliveryMan, user.getUserId(), box.getBoxId(), deliveryType, new RestfulResult() {
            @Override
            public void doResult(RfResultEvent event) {
                if(event.getResult() == RfResultEvent.OK){
                    Speaker.deliverySucess();
                    if(parent.equals("selectRoom")){
                        app.goCommonDelivery();
                    }
                    else {
                        app.goDelivery();
                    }
                }
                else {
                    Speaker.deliveryFail();
                }
            }

            @Override
            public void doFault(RfFaultEvent event) {
                Speaker.deliveryFail();
            }
        });
    }

}
