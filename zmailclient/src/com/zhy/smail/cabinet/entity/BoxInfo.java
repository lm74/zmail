package com.zhy.smail.cabinet.entity;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Created by wenliz on 2017/2/11.
 */
public class BoxInfo {
    public final static int BOX_TYPE_MAIL = 0;
    public final static int BOX_TYPE_SMALL = 1;
    public final static int BOX_TYPE_MIDDLE = 2;
    public final static int BOX_TYPE_LARGE = 3;

    private Integer boxId;
    private Integer cabinetId;
    private Integer controlCardId;

    public Integer getBoxId() {
        return boxId;
    }

    public void setBoxId(Integer boxId) {
        this.boxId = boxId;
    }

    public Integer getCabinetId() {
        return cabinetId;
    }

    public void setCabinetId(Integer cabinetId) {
        this.cabinetId = cabinetId;
    }

    public Integer getControlCardId() {
        return controlCardId;
    }

    public void setControlCardId(Integer controlCardId) {
        this.controlCardId = controlCardId;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public Integer getBoxType() {
        return boxType;
    }

    public void setBoxType(Integer boxType) {
        this.boxType = boxType;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public boolean isOpened() {
        return opened;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    public Integer getDeliveryMan() {
        return deliveryMan;
    }

    public void setDeliveryMan(Integer deliveryMan) {
        this.deliveryMan = deliveryMan;
    }

    public Integer getOwner() {
        return owner;
    }

    public void setOwner(Integer owner) {
        this.owner = owner;
    }
    private Integer controlSequence;

    public Integer getControlSequence() {
        return controlSequence;
    }

    public void setControlSequence(Integer controlSequence) {
        this.controlSequence = controlSequence;
    }

    private Integer sequence;
    private Integer boxType;
    private boolean locked;
    private boolean used;
    private boolean opened;
    private Integer deliveryMan;
    private Integer owner;

    public String toString(){
        return String.valueOf(this.controlSequence);
    }

    public Button createButton(boolean checked){
        String styleClass;
        String title = String.valueOf(getSequence()) + " ";
        if(isLocked()){
            title +="锁定";
            styleClass = "locked-button";
        }
        else if(isUsed()){
            title += "占用";
            styleClass="full-button";
        }
        else if(isOpened()){
            title += "开门";
            styleClass = "full-button";
        }
        else {
            title += "空闲";
            styleClass = "empty-button";
        }
        int height = 60;
        Button button;
        switch (getBoxType()){
            case BoxInfo.BOX_TYPE_MAIL:
                height = 40;
                Image image;
                if(checked) {
                    image =new Image(getClass().getResourceAsStream("/resources/images/button/check.png"));
                }
                else{
                    image = new  Image(getClass().getResourceAsStream("/resources/images/button/email.png"));
                }

                ImageView imageView = new ImageView();
                imageView.setImage(image);
                button = new Button(title, imageView);
                break;
            case BoxInfo.BOX_TYPE_SMALL:
                height = 40;
                button = new Button(title);
                break;
            case BoxInfo.BOX_TYPE_MIDDLE:
                height = 80;
                button = new Button(title);
                break;
            case BoxInfo.BOX_TYPE_LARGE:
                button = new Button(title);
                height = 120;
                break;
            default:
                button = new Button(title);
                break;
        }

        button.setPrefWidth(200);
        button.getStyleClass().add(styleClass);
        button.setPrefHeight(height);
        return button;
    }
}
