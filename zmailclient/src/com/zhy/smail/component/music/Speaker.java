package com.zhy.smail.component.music;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * Created by wenliz on 2017/2/22.
 */
public class Speaker {
    /**
     * 欢迎使用智能信包箱
     */
    public static void welcome(){
        playMusic("/music/0.wav");
    }

    /*
    *箱门已开,请随手关门
     */
    public static void openBox(){
        playMusic("/music/1.wav");
    }

    /*
   *请输入房号或投递员账号
    */
    public static void roomOrDelivery(){
        playMusic("/music/2.wav");
    }

    /*
   *箱门故障请联系管理员
    */
    public static void boxFault(){
        playMusic("/music/3.wav");
    }

    /*
  *请输入投递员账号
   */
    public static void inputUserName(){
        playMusic("/music/4.wav");
    }

    /*
    *请输入业主房号
     */
    public static void inputRoom(){
       playMusic("/music/5.wav");
    }

    /*
    *请输入密码
     */
    public static void inputPassword(){
        playMusic("/music/6.wav");
    }
    /*
    *请选择投递类型
     */
    public static void deliveryType(){
        playMusic("/music/7.wav");
    }

    /*
    *请选择箱门大小
     */
    public static void boxSize(){
        playMusic("/music/8.wav");
    }

    /*
   *请选择业主房号
    */
    public static void choseRoomNo(){
        playMusic("/music/9.wav");
    }

    /*
   *箱门已开，请放入物品后，点击确认投递
    */
    public static void delivery(){
        playMusic("/music/10.wav");
    }

    /*
   *投递成功
    */
    public static void deliverySucess(){
        playMusic("/music/11.wav");
    }

    /*
   *投递失败
    */
    public static void deliveryFail(){
        playMusic("/music/12.wav");
    }

    /*
   *没有物品寄存
    */
    public static void noPacket(){
        playMusic("/music/99.wav");
    }

    /*
   *箱门已开启，取出物品后，随手关好箱门
    */
    public static void doorOpened(){
        playMusic("/music/14.wav");
    }

    /*
*无效卡
 */
    public static void invalideCard(){
        playMusic("/music/15.wav");
    }

    /*
 *箱门未关，请关好箱门
  */
    public static void doorNoClosed(){
        playMusic("/music/16.wav");
    }

    /*
  *请输入要查询的房号
   */
    public static void inputQueryRoomNo(){
        playMusic("/music/17.wav");
    }

    /*
  *键盘声音
   */
    public static void keyTypeSound(){
        playMusic("/music/18.wav");
    }

    /*
  *开门声音
   */
    public static void openBoxSound(){
        playMusic("/music/19.wav");
    }

    /**
     * 请输入管理员帐号
     */
    public static void inputManagerUser() {playMusic("/music/20.wav");}

    /**
     * 用户名或密码错
     */
    public  static  void userOrPasswordError(){playMusic("/music/22.wav");}

    private static void playMusic(String path){
        String music0 = Speaker.class.getResource(path).toString();
        Media media2 = new Media(music0);
        MediaPlayer mp2 = new MediaPlayer(media2);
        mp2.play();

        mp2.setOnStopped(new Runnable() {
            @Override
            public void run() {

            }
        });

    }
}
