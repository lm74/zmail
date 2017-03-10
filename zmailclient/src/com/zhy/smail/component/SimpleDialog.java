package com.zhy.smail.component;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Created by wenliz on 2017/1/20.
 */

public class SimpleDialog {

    public enum Response {
        NO, YES, CANCEL
    };

    private static Response buttonSelected = Response.CANCEL;

    private static ImageView icon = new ImageView();

    public static class Dialog extends Stage {
        public Dialog(String title, Stage owner, Scene scene) {
            scene.getStylesheets().add("style.css");
            setTitle(title);
            initStyle(StageStyle.UNDECORATED);
            initModality(Modality.APPLICATION_MODAL);
            initOwner(owner);
            setResizable(true);
            setScene(scene);
            icon.setImage(new Image(getClass().getResourceAsStream("/images/icon/light.png")));
        }

        public void showDialog() {
            sizeToScene();
            centerOnScreen();
            showAndWait();
        }

        public void showX(){
            sizeToScene();
            centerOnScreen();
            show();
        }
    }

    static class Message extends Text {
        public Message(String msg) {
            super(msg);
            setWrappingWidth(800);//自动换行的宽度
        }
    }

    public static Response showConfirmDialog(Stage owner, String message,String title) {
        VBox vb = new VBox();
        Scene scene = new Scene(vb);
        final Dialog dial = new Dialog(title, owner, scene);
        vb.setPadding(new Insets(10, 10, 10, 10));
        vb.setSpacing(10);
        Button yesButton = new Button("确定");
        yesButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                dial.close();
                buttonSelected = Response.YES;
            }
        });
        Button noButton = new Button("取消");
        noButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                dial.close();
                buttonSelected = Response.NO;
            }
        });
        BorderPane bp = new BorderPane();
        HBox buttons = new HBox();
        buttons.setAlignment(Pos.CENTER);
        buttons.setSpacing(10);
        buttons.getChildren().addAll(yesButton, noButton);
        bp.setCenter(buttons);
        HBox msg = new HBox();
        msg.setSpacing(5);
        msg.getChildren().addAll(icon,new Message(message));
        vb.getChildren().addAll(msg, bp);
        dial.showDialog();
        return buttonSelected;
    }
    public static void showMessageDialog(Stage owner, String message,String title) {
        showMessageDialog(owner, new Message(message), title);
    }

    //用以识别html语句
    public static void showWebViewDialog(Stage owner,String text_,String title){
        VBox vb = new VBox();
        Scene scene = new Scene(vb);
        final Dialog dial = new Dialog(title, owner, scene);
        vb.setPadding(new Insets(10, 10, 10, 10));
        vb.setSpacing(10);
        Button okButton = new Button("确定");
        okButton.setAlignment(Pos.CENTER);
        okButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                dial.close();
            }
        });
        BorderPane bp = new BorderPane();
        bp.setCenter(okButton);
        HBox msg = new HBox();
        msg.setSpacing(5);
        WebView webview = new WebView();
        WebEngine engine = webview.getEngine();
        engine.loadContent(text_);
        msg.getChildren().addAll(icon, webview);
        vb.getChildren().addAll(msg, bp);
        dial.setMinHeight(150);
        dial.setMaxHeight(150);
        dial.setHeight(150);
        dial.showDialog();

    }
    public static void showMessageDialog(Stage owner, Node message, String title) {
        VBox vb = new VBox();
        Scene scene = new Scene(vb);
        final Dialog dial = new Dialog(title, owner, scene);
        vb.setPadding(new Insets(10, 10, 10, 10));
        vb.setSpacing(10);
        Button okButton = new Button("确定");
        okButton.setAlignment(Pos.CENTER);
        okButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                dial.close();
            }
        });
        BorderPane bp = new BorderPane();
        bp.setCenter(okButton);
        HBox msg = new HBox();
        msg.setSpacing(5);
        msg.getChildren().addAll(icon, message);
        vb.getChildren().addAll(msg, bp);
        dial.showDialog();
    }

    public static void showAutoCloseError(Stage owner, String message){
        showAutocloseDialog(owner, message, 3000, false);
    }

    public static void showAutoCloseInfo(Stage owner, String message){
        showAutocloseDialog(owner, message, 1000, true);
    }

    public static void showAutocloseDialog(Stage owner, String message, Integer waitTime, boolean success){
        Task<Integer> task = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {
                updateMessage(message);
                try {
                    Thread.sleep(waitTime);
                }
                catch (Exception e){

                }
                updateValue(0);
                return 0;
            }
        };

        VBox vb = new VBox();
        Scene scene = new Scene(vb);

        final Dialog dialog = new Dialog("", owner, scene);
        vb.setPadding(new Insets(10, 10, 10, 10));
        vb.setSpacing(10);
        Button okButton = new Button("确定");
        okButton.setAlignment(Pos.CENTER);
        okButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                dialog.close();
            }
        });
        BorderPane bp = new BorderPane();
        bp.setCenter(okButton);
        okButton.setVisible(false);

        HBox msg = new HBox();
        msg.setSpacing(30);
        ImageView headIcon = new ImageView();
        if(success) {
            headIcon.setImage(new Image(dialog.getClass().getResourceAsStream("/resources/images/button/check.png")));
        }
        else{
            headIcon.setImage(new Image(dialog.getClass().getResourceAsStream("/resources/images/button/error.png")));
        }
        msg.getChildren().add(headIcon);
        Message text = new Message(message);
        msg.getChildren().add(text);
        vb.getChildren().addAll(msg, bp);
        text.textProperty().bind(task.messageProperty());

        task.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if(newValue == null) return;

                Integer realValue = (Integer) newValue;
                if(realValue >= 0){
                    dialog.close();
                }
                else if(realValue == -1){
                    okButton.setVisible(true);
                }
            }
        });

        new Thread(task).start();
        dialog.showDialog();
    }

    public static void showDialog(Stage owner, Task task, String message, String title){
        VBox vb = new VBox();
        Scene scene = new Scene(vb);

        final Dialog dialog = new Dialog(title, owner, scene);
        vb.setPadding(new Insets(10, 10, 10, 10));
        vb.setSpacing(10);
        Button okButton = new Button("确定");
        okButton.setAlignment(Pos.CENTER);
        okButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                dialog.close();
            }
        });
        BorderPane bp = new BorderPane();
        bp.setCenter(okButton);
        okButton.setVisible(false);

        HBox msg = new HBox();
        msg.setSpacing(5);
        final ProgressIndicator mBar = new ProgressIndicator(0);
        //mBar.setMaxSize(550, 550);
        msg.getChildren().add(mBar);
        Message text = new Message(message);
        msg.getChildren().add(text);
        vb.getChildren().addAll(msg, bp);

        mBar.progressProperty().bind(task.progressProperty());
        text.textProperty().bind(task.messageProperty());

        task.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if(newValue == null) return;

                Integer realValue = (Integer) newValue;
                if(realValue >= 0){
                    dialog.close();
                }
                else if(realValue == -1){
                    mBar.setVisible(false);
                    okButton.setVisible(true);
                }
            }
        });

        new Thread(task).start();

        dialog.showDialog();
    }

    public static Stage modelDialog(Stage owner, Task task, String message, String title) {
        Region veil = new Region();
       // veil.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4)");

        HBox mHbox = new HBox(10);
        final ProgressIndicator mBar = new ProgressIndicator(0);
        mBar.setMaxSize(250, 250);
        Label mLabel = new Label(message);
        Insets padding = new Insets(30,10,0,10);
        mLabel.setPadding(padding);


        //mLabel.setStyle("-fx-font-size: 12pt");
        final Button okButton = new Button("确定");
        okButton.setAlignment(Pos.CENTER);
        Insets margin = new Insets(25,0,0,0);
        mHbox.setMargin(okButton, margin);
        okButton.setVisible(false);
        //okButton.setStyle("-fx-font-size: 12pt");
        mHbox.getChildren().add(mBar);
        mHbox.getChildren().add(mLabel);
        mHbox.getChildren().add(okButton);
        StackPane root = new StackPane();
        root.getChildren().add(mHbox);
        Scene scene = new Scene(root, 550, 80);


        veil.visibleProperty().bind(task.runningProperty());
        mBar.progressProperty().bind(task.progressProperty());
        mLabel.textProperty().bind(task.messageProperty());

        final Stage progressDialog = new Stage(StageStyle.UNDECORATED);
        progressDialog.initOwner(owner);
        progressDialog.setTitle(title);
        progressDialog.initModality(Modality.APPLICATION_MODAL);
        progressDialog.setResizable(false);
        progressDialog.setFullScreen(false);
        progressDialog.setScene(scene);
        progressDialog.show();

        okButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                progressDialog.close();
            }
        });

        task.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if(newValue == null) return;

                Integer realValue = (Integer) newValue;
                if(realValue == 0){
                    progressDialog.close();
                }
                else if(realValue == -1){
                    mBar.setVisible(false);
                    okButton.setVisible(true);
                }
            }
        });




        new Thread(task).start();
        return progressDialog;
    }
}