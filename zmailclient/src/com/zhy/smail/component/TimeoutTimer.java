package com.zhy.smail.component;

import javafx.application.Platform;
import javafx.scene.control.Label;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wenliz on 2017/1/24.
 */
public class TimeoutTimer {
    public static final int DOOR_TIMEOUR = 30;
    private Label title;
    private int total;
    private int interval;
    private int perTotal;
    private Timer timer;
    private TimeoutCallback callback;

    public  TimeoutTimer(Label title, int total, TimeoutCallback callback){
        this.title = title;
        this.total = total;
        this.interval = 1000;
        this.callback = callback;
    }

    public TimeoutTimer(int total, int interval, TimeoutCallback callback){
        this.title = null;
        this.total = total;
        this.interval = interval;
        this.callback = callback;
    }

    public void start(){
        perTotal = total;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                perTotal--;
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if(title!=null) {
                            title.setText(String.valueOf(perTotal));
                        }
                    }
                });

                if(perTotal == 0){
                    timer.cancel();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            callback.run();
                        }
                    });
                }
            }
        }, 0, interval);
    }

    public void restart(){
        if(timer != null){
            timer.cancel();
            timer = null;
        }
        start();
    }

    public void cancel(){
        if(timer != null){
            timer.cancel();
            timer = null;
        }
    }

    public interface TimeoutCallback{
        public void run();
    }
}
