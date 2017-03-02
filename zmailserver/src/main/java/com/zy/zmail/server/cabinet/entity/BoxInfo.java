package com.zy.zmail.server.cabinet.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by wenliz on 2017/2/10.
 */
@Entity
@Table(name="BoxInfo")
public class BoxInfo implements Serializable {
    public final static int BOX_TYPE_MAIL = 0;
    public final static int BOX_TYPE_SMALL = 1;
    public final static int BOX_TYPE_MIDDLE = 2;
    public final static int BOX_TYPE_LARGE = 3;

    @SequenceGenerator(name="sq_boxinfo", sequenceName = "sq_boxinfo", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_boxinfo")
    @Id
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

}
