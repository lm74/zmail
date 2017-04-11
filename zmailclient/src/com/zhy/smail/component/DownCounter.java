package com.zhy.smail.component;

import javafx.application.Platform;
import javafx.scene.control.Label;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wenliz on 2017/4/10.
 */
public class DownCounter {
    public static final int DOOR_TIMEOUR = 30;
    private Label title;

    public Label getTitle() {
        return title;
    }

    public void setTitle(Label title) {
        this.title = title;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    private int total;

    private DownCounter.TimeoutCallback callback;
    DownExecutor executor = null;
    Thread executorThread= null;

    public  DownCounter(Label title, int total, DownCounter.TimeoutCallback callback){
        this.title = title;
        this.total = total;

        this.callback = callback;
    }



    public void start(){
        if(executor == null) {
            executor = new DownExecutor(title, total, callback);
            executorThread = new Thread(executor,"DownExecutor");
            executorThread.start();
        }
        else{
            executor.setTitle(title);
            executor.setTotal(total);
            executor.setCallback(callback);
            executor.setFinished(false);
        }

    }

    public void restart(){
        start();
    }

    public void destroy(){
        if(executor != null){
            executor.setCanceled(true);
            try {
                executorThread.join();
            }
            catch (Exception e){

            }
            finally {
                executor = null;
                executorThread = null;
            }
        }
    }

    public interface TimeoutCallback{
        public void run();
    }

    public class DownExecutor implements  Runnable{
        private boolean canceled;
        private Label title;
        private int total;
        private boolean finished;

        public boolean isFinished() {
            return finished;
        }

        public void setFinished(boolean finished) {
            this.finished = finished;
        }

        public Label getTitle() {
            return title;
        }

        public void setTitle(Label title) {
            this.title = title;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
            this.perTotal = total;
        }

        private int perTotal;

        public boolean isCanceled() {
            return canceled;
        }

        public void setCanceled(boolean canceled) {
            this.canceled = canceled;
        }

        private DownCounter.TimeoutCallback callback;

        public TimeoutCallback getCallback() {
            return callback;
        }

        public void setCallback(TimeoutCallback callback) {
            this.callback = callback;
        }

        public DownExecutor(Label title, int total, DownCounter.TimeoutCallback callback){
            this.title = title;
            this.total = total;
            this.callback = callback;
            this.perTotal = total;
            this.canceled = false;
            this.finished = false;
        }
        public void run(){
            while (!canceled){
                if(!finished) {
                    perTotal--;
                    if (title != null) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                if (title != null) {
                                    title.setText(String.valueOf(perTotal));
                                }
                            }
                        });
                    }

                    if (perTotal == 0) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                callback.run();
                            }
                        });
                        finished = true;
                    }
                }

                try{
                    Thread.sleep(1000);
                }
                catch (Exception e){

                }
            }
        }
    }
}

