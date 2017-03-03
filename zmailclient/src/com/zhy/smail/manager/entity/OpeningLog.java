package com.zhy.smail.manager.entity;

import com.zhy.smail.cabinet.entity.BoxInfo;
import com.zhy.smail.user.entity.UserInfo;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.sql.Timestamp;
import java.text.DateFormat;

/**
 * Created by wenliz on 2017/3/2.
 */
public class OpeningLog {
    private Integer logId;

    public Integer getLogId() {
        return logId;
    }

    public void setLogId(Integer logId) {
        this.logId = logId;
        this.logIdProperty = new SimpleIntegerProperty(logId);
    }

    public SimpleIntegerProperty logIdPropertyProperty() {
        return logIdProperty;
    }


    public Timestamp getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(Timestamp openingTime) {
        this.openingTime = openingTime;

        if(openingTime == null){
            openingTimeProperty = new SimpleStringProperty(this, "openingTime", "");
        }
        else{
            DateFormat format = DateFormat.getDateTimeInstance();
            openingTimeProperty = new SimpleStringProperty(this, "openingTime", format.format(openingTime));
        }
    }


    public SimpleStringProperty openingTimePropertyProperty() {
        return openingTimeProperty;
    }


    public UserInfo getOpeningUser() {
        return openingUser;
    }

    public void setOpeningUser(UserInfo openingUser) {
        this.openingUser = openingUser;

        if(this.openingUser == null){
            openingUserProperty = new SimpleStringProperty(this, "openingUser", "");
        }
        else{
            openingUserProperty = new SimpleStringProperty(this, "openingUser", openingUser.getUserName());
        }
    }

    public ReadOnlyStringProperty openingUserPropertyProperty() {
        return openingUserProperty;
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



    public String getOpeningResult() {
        return openingResult;
    }

    public void setOpeningResult(String openingResult) {
        this.openingResult = openingResult;
        if(openingResult == null){
            openingResultProperty = new SimpleStringProperty(this, "openingResult", "");
        }
        else{
            openingResultProperty = new SimpleStringProperty(this, "openingResult", openingResult);
        }
    }

    public SimpleStringProperty openingResultPropertyProperty() {
        return openingResultProperty;
    }


    private SimpleIntegerProperty logIdProperty;
    private Timestamp openingTime;
    private SimpleStringProperty openingTimeProperty;
    private UserInfo openingUser;
    private SimpleStringProperty openingUserProperty;
    private BoxInfo boxInfo;
    private SimpleIntegerProperty boxInfoProperty;
    private String openingResult;
    private SimpleStringProperty openingResultProperty;

}
