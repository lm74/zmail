package com.zhy.smail;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;


/**
 * Created by Administrator on 2017/3/7.
 */
public class FirstMainController {
    MainApp app;
    Stage stage;


    public void setApp(MainApp app) {
        this.app = app;
    }

    @FXML
    public void goMain(ActionEvent actionEvent){
        try {
            app.starMain(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
