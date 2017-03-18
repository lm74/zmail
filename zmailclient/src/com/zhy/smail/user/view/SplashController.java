package com.zhy.smail.user.view;

import com.zhy.smail.MainApp;
import com.zhy.smail.common.controller.RootController;
import com.zhy.smail.config.LocalConfig;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by wenliz on 2017/3/13.
 */
public class SplashController extends RootController implements Initializable {
    @FXML
    private MediaView welcomeView;
    private Media videoMeida;
    private MediaPlayer mediaPlayer;

    @Override
    public void setApp(MainApp app){
        this.app = app;

        welcomeView.setFitHeight(app.getRootStage().getHeight());
        welcomeView.setFitWidth(app.getRootStage().getWidth());
        welcomeView.setPreserveRatio(false);

    }

    public boolean play(){
        try {

            String viedoFile = LocalConfig.getInstance().getVideoFile();
            File file = new File(viedoFile);
            videoMeida = new Media(file.toURI().toString());
            mediaPlayer = new MediaPlayer(videoMeida);

            welcomeView.setMediaPlayer(mediaPlayer);

            mediaPlayer.setAutoPlay(true);
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public void initialize(URL location, ResourceBundle resources){



    }

    private void stopPlay(){
        mediaPlayer.stop();
        mediaPlayer.dispose();
        mediaPlayer=null;
    }

    @FXML
    private void onMouseClicked(MouseEvent event){
        stopPlay();
        app.goHome();
    }

    @FXML
    private void onKeypressed(KeyEvent event){
        stopPlay();
        app.goHome();
    }
}
