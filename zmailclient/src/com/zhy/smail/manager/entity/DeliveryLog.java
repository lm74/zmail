package com.zhy.smail.manager.entity;

import com.zhy.smail.cabinet.entity.BoxInfo;
import com.zhy.smail.config.GlobalOption;
import com.zhy.smail.user.entity.UserInfo;
import javafx.beans.property.*;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by wenliz on 2017/2/14.
 */
public class DeliveryLog {
    public final static int THIS_DATE = 1;
    public final static int THIS_WEEK = 2;
    public final static int THIS_MONTH = 3;
    public final static int RECENT_ONE_MONTH = 4;
    public final static int RECENT_THREE_MONTH = 5;
    public final static int DELIVERY_TYPE_MAIL = 0;

    private Integer logId;
    private SimpleIntegerProperty logIdProperty;
    private Timestamp deliveryTime;
    private SimpleStringProperty deliveryTimeProperty;
    private UserInfo deliveryMan;
    private SimpleStringProperty deliveryManProperty;
    private Integer deliveryType;
    private SimpleStringProperty deliveryTypeProperty;
    /*private SimpleIntegerProperty overTime;

    public int getOverTime() {
        return overTime.get();
    }

    public SimpleIntegerProperty overTimeProperty() {
        return overTime;
    }*/



    public Integer getLogId() {
        return logId;
    }

    public void setLogId(Integer logId) {
        this.logId = logId;
        this.logIdProperty = new SimpleIntegerProperty(logId);
    }
    public SimpleIntegerProperty logIdProperty(){
        return logIdProperty;
    }

    public Timestamp getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Timestamp deliveryTime) {
        this.deliveryTime = deliveryTime;


        if(deliveryTime == null){
            deliveryTimeProperty = new SimpleStringProperty(this, "deliveryTime", "");
        }
        else{

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            deliveryTimeProperty = new SimpleStringProperty(this, "deliveryTime", format.format(deliveryTime));
        }

        /*if(GlobalOption.timeout.getIntValue() ==0){
            overTime.set(0);
        }
        else{
            long inteval = deliveryTime.getTime() - System.currentTimeMillis();
            overTime.set(Long.valueOf(inteval /1000/60/60).intValue());
        }*/
    }

    public ReadOnlyStringProperty delivertyTimeProperty(){
        return deliveryTimeProperty;
    }

    public UserInfo getDeliveryMan() {
        return deliveryMan;
    }

    public void setDeliveryMan(UserInfo deliveryMan) {
        this.deliveryMan = deliveryMan;
        if(deliveryMan == null){
            deliveryManProperty = new SimpleStringProperty(this, "deliveryMan", "");
        }
        else{
            deliveryManProperty = new SimpleStringProperty(this, "deliveryMan", deliveryMan.getUserName());
        }
    }

    public ReadOnlyStringProperty deliveryManProperty(){
        return deliveryManProperty;
    }

    public Integer getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(Integer deliveryType) {
        this.deliveryType = deliveryType;
        if(deliveryType == null){
            deliveryTypeProperty = new SimpleStringProperty(this, "deliveryType", "");
        }
        else{
            if(deliveryType == 0){
                deliveryTypeProperty = new SimpleStringProperty(this, "deliveryType","信件");
            }
            else{
                deliveryTypeProperty = new SimpleStringProperty(this, "deliveryType","包裹");
            }
        }
    }

    public ReadOnlyStringProperty deliveryTypeProperty(){
        return deliveryTypeProperty;
    }

    public BoxInfo getBoxInfo() {
        return boxInfo;
    }

    public void setBoxInfo(BoxInfo boxInfo) {
        this.boxInfo = boxInfo;
        if(boxInfo == null){
            boxInfoProperty = new SimpleIntegerProperty(this, "boxSequence", 0);
        }
        else{
            boxInfoProperty = new SimpleIntegerProperty(this, "boxSequence", boxInfo.getSequence());
        }
    }
    public ReadOnlyIntegerProperty boxSequenceProperty(){
        return boxInfoProperty;
    }

    public Timestamp getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(Timestamp pickupTime) {
        this.pickupTime = pickupTime;

        if(pickupTime == null){
            pickupTimeProperty = new SimpleStringProperty(this, "pickupTime", "");
        }
        else{
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            pickupTimeProperty = new SimpleStringProperty(this, "pickupTime", format.format(pickupTime));
        }
    }
    public ReadOnlyStringProperty pickupTimeProperty(){
        return pickupTimeProperty;
    }

    public UserInfo getPickupUser() {
        return pickupUser;
    }

    public void setPickupUser(UserInfo pickupUser) {
        this.pickupUser = pickupUser;
        if(pickupUser == null){
            pickupUserProperty = new SimpleStringProperty(this, "pickupUser", "");
        }
        else{
            pickupUserProperty = new SimpleStringProperty(this, "pickupUser", pickupUser.getUserName());
        }
    }

    public ReadOnlyStringProperty pickupUserProperty(){
        return pickupUserProperty;
    }

    public Integer getPickupType() {
        return pickupType;
    }

    public void setPickupType(Integer pickupType) {
        this.pickupType = pickupType;
    }

    private BoxInfo boxInfo;
    private SimpleIntegerProperty boxInfoProperty;
    private Timestamp pickupTime;
    private SimpleStringProperty pickupTimeProperty;
    private UserInfo pickupUser;
    private SimpleStringProperty pickupUserProperty;
    private Integer pickupType;

}
