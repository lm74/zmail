package com.zhy.smail.cabinet.entity;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Created by wenliz on 2017/2/8.
 */
public class CabinetProperty {
    private SimpleIntegerProperty cabinetId;
    private SimpleStringProperty cabinetNo;
    private SimpleIntegerProperty boxNumber;

    private SimpleIntegerProperty controller1BoxNumber;
    private SimpleIntegerProperty controller2BoxNumber;
    private SimpleIntegerProperty controller3BoxNumber;
    private SimpleIntegerProperty controller4BoxNumber;
    private SimpleIntegerProperty controller5BoxNumber;
    private SimpleIntegerProperty controller6BoxNumber;
    private SimpleIntegerProperty controller7BoxNumber;
    private SimpleIntegerProperty controller8BoxNumber;
    private SimpleIntegerProperty controller9BoxNumber;
    private SimpleIntegerProperty controller10BoxNumber;
    private CabinetInfo data;

    public CabinetInfo getData() {
        return data;
    }

    public void setData(CabinetInfo data) {
        this.data = data;
    }

    public CabinetProperty() {
        cabinetId = new SimpleIntegerProperty();
        cabinetNo = new SimpleStringProperty();
        boxNumber = new SimpleIntegerProperty();
        controller1BoxNumber = new SimpleIntegerProperty();
        controller2BoxNumber = new SimpleIntegerProperty();
        controller3BoxNumber = new SimpleIntegerProperty();
        controller4BoxNumber = new SimpleIntegerProperty();
        controller5BoxNumber = new SimpleIntegerProperty();
        controller6BoxNumber = new SimpleIntegerProperty();
        controller7BoxNumber = new SimpleIntegerProperty();
        controller8BoxNumber = new SimpleIntegerProperty();
        controller9BoxNumber = new SimpleIntegerProperty();
        controller10BoxNumber = new SimpleIntegerProperty();
    }


    public int getCabinetId() {
        return cabinetId.get();
    }

    public SimpleIntegerProperty cabinetIdProperty() {
        return cabinetId;
    }

    public void setCabinetId(int cabinetId) {
        this.cabinetId.set(cabinetId);
    }

    public String getCabinetNo() {
        return cabinetNo.get();
    }

    public SimpleStringProperty cabinetNoProperty() {
        return cabinetNo;
    }

    public void setCabinetNo(String cabinetNo) {
        this.cabinetNo.set(cabinetNo);
    }

    public int getBoxNumber() {
        return boxNumber.get();
    }

    public SimpleIntegerProperty boxNumberProperty() {
        return boxNumber;
    }

    public void setBoxNumber(int boxNumber) {
        this.boxNumber.set(boxNumber);
    }

    public int getController1BoxNumber() {
        return controller1BoxNumber.get();
    }

    public SimpleIntegerProperty controller1BoxNumberProperty() {
        return controller1BoxNumber;
    }

    public void setController1BoxNumber(int controller1BoxNumber) {
        this.controller1BoxNumber.set(controller1BoxNumber);
    }

    public int getController2BoxNumber() {
        return controller2BoxNumber.get();
    }

    public SimpleIntegerProperty controller2BoxNumberProperty() {
        return controller2BoxNumber;
    }

    public void setController2BoxNumber(int controller2BoxNumber) {
        this.controller2BoxNumber.set(controller2BoxNumber);
    }

    public int getController3BoxNumber() {
        return controller3BoxNumber.get();
    }

    public SimpleIntegerProperty controller3BoxNumberProperty() {
        return controller3BoxNumber;
    }

    public void setController3BoxNumber(int controller3BoxNumber) {
        this.controller3BoxNumber.set(controller3BoxNumber);
    }

    public int getController4BoxNumber() {
        return controller4BoxNumber.get();
    }

    public SimpleIntegerProperty controller4BoxNumberProperty() {
        return controller4BoxNumber;
    }

    public void setController4BoxNumber(int controller4BoxNumber) {
        this.controller4BoxNumber.set(controller4BoxNumber);
    }

    public int getController5BoxNumber() {
        return controller5BoxNumber.get();
    }

    public SimpleIntegerProperty controller5BoxNumberProperty() {
        return controller5BoxNumber;
    }

    public void setController5BoxNumber(int controller5BoxNumber) {
        this.controller5BoxNumber.set(controller5BoxNumber);
    }

    public int getController6BoxNumber() {
        return controller6BoxNumber.get();
    }

    public SimpleIntegerProperty controller6BoxNumberProperty() {
        return controller6BoxNumber;
    }

    public void setController6BoxNumber(int controller6BoxNumber) {
        this.controller6BoxNumber.set(controller6BoxNumber);
    }

    public int getController7BoxNumber() {
        return controller7BoxNumber.get();
    }

    public SimpleIntegerProperty controller7BoxNumberProperty() {
        return controller7BoxNumber;
    }

    public void setController7BoxNumber(int controller7BoxNumber) {
        this.controller7BoxNumber.set(controller7BoxNumber);
    }

    public int getController8BoxNumber() {
        return controller8BoxNumber.get();
    }

    public SimpleIntegerProperty controller8BoxNumberProperty() {
        return controller8BoxNumber;
    }

    public void setController8BoxNumber(int controller8BoxNumber) {
        this.controller8BoxNumber.set(controller8BoxNumber);
    }

    public int getController9BoxNumber() {
        return controller9BoxNumber.get();
    }

    public SimpleIntegerProperty controller9BoxNumberProperty() {
        return controller9BoxNumber;
    }

    public void setController9BoxNumber(int controller9BoxNumber) {
        this.controller9BoxNumber.set(controller9BoxNumber);
    }

    public int getController10BoxNumber() {
        return controller10BoxNumber.get();
    }

    public SimpleIntegerProperty controller10BoxNumberProperty() {
        return controller10BoxNumber;
    }

    public void setController10BoxNumber(int controller10BoxNumber) {
        this.controller10BoxNumber.set(controller10BoxNumber);
    }

    public static  CabinetProperty valueOf(CabinetInfo cabinetInfo){
        CabinetProperty cabinet = new CabinetProperty();
        if(cabinetInfo.getCabinetId()!=null){
            cabinet.setCabinetId(cabinetInfo.getCabinetId());
        }

        cabinet.setCabinetNo(cabinetInfo.getCabinetNo());
        if(cabinetInfo.getBoxNumber() != null){
            cabinet.setBoxNumber(cabinetInfo.getBoxNumber());
        }
        if(cabinetInfo.getController1BoxNumber() != null){
            cabinet.setController1BoxNumber(cabinetInfo.getController1BoxNumber());
        }
        if(cabinetInfo.getController2BoxNumber() != null){
            cabinet.setController2BoxNumber(cabinetInfo.getController2BoxNumber());
        }
        if(cabinetInfo.getController3BoxNumber() != null){
            cabinet.setController3BoxNumber(cabinetInfo.getController3BoxNumber());
        }
        if(cabinetInfo.getController4BoxNumber() != null){
            cabinet.setController4BoxNumber(cabinetInfo.getController4BoxNumber());
        }
        if(cabinetInfo.getController5BoxNumber() != null){
            cabinet.setController5BoxNumber(cabinetInfo.getController5BoxNumber());
        }
        if(cabinetInfo.getController6BoxNumber() != null){
            cabinet.setController6BoxNumber(cabinetInfo.getController6BoxNumber());
        }
        if(cabinetInfo.getController7BoxNumber() != null){
            cabinet.setController7BoxNumber(cabinetInfo.getController7BoxNumber());
        }
        if(cabinetInfo.getController8BoxNumber() != null){
            cabinet.setController8BoxNumber(cabinetInfo.getController8BoxNumber());
        }
        if(cabinetInfo.getController9BoxNumber() != null){
            cabinet.setController9BoxNumber(cabinetInfo.getController9BoxNumber());
        }
        if(cabinetInfo.getController10BoxNumber() != null){
            cabinet.setController10BoxNumber(cabinetInfo.getController10BoxNumber());
        }

        cabinet.setData(cabinetInfo);
        return cabinet;

    }
}

