package com.zhy.smail.component.music;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

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


    private static String getWorkDir() {
        // 得到当前应用程序（无论是class还是Jar包）的绝对根路径
        URL url = Speaker.class.getProtectionDomain().getCodeSource().getLocation();
        String file = null;
        try {
            file = URLDecoder.decode(url.getPath(), "UTF-8"); // 路径如果包含中文进行编码转换
        } catch (UnsupportedEncodingException e) {

        }


        // 掐去第一个字符"/"
        if (file.startsWith("/")) {
            file = file.substring(1);
        }


        // 去掉尾部的"xxx.jar"字样
        if (file.endsWith(".jar")) {
            file = file.substring(0, file.lastIndexOf("/") + 1);
        }

        return file;
    }

    private static void playMusic(String path){
        try {
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            String music0 = "file:/"+getWorkDir() + path;
            System.out.println(music0);
            Media media2 = new Media(music0);
            MediaPlayer mp2 = new MediaPlayer(media2);
            mp2.setOnEndOfMedia(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Playing End.");
                }
            });
            mp2.setOnError(new Runnable() {
                @Override
                public void run() {
                    String errorMessage = mp2.getError().getMessage();
                    System.out.println("MediaPlayer Error: " + errorMessage);
                }
            });
            mp2.play();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }

    }
}
