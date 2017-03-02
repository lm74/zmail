package com.zy.zmail.server.delivery.entity;

import com.zy.zmail.server.cabinet.entity.BoxInfo;
import com.zy.zmail.server.user.entity.UserInfo;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by wenliz on 2017/2/14.
 */
@Entity
@Table(name="DeliveryLog")
public class LogBrief implements Serializable {
    @SequenceGenerator(name="sq_deliverylog", sequenceName = "sq_deliverylog", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_deliverylog")
    @Id
    private Integer logId;
    private Timestamp deliveryTime;
    private Integer deliveryMan;

    public Integer getLogId() {
        return logId;
    }

    public void setLogId(Integer logId) {
        this.logId = logId;
    }

    public Timestamp getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Timestamp deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public Integer getDeliveryMan() {
        return deliveryMan;
    }

    public void setDeliveryMan(Integer deliveryMan) {
        this.deliveryMan = deliveryMan;
    }

    public Integer getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(Integer deliveryType) {
        this.deliveryType = deliveryType;
    }

    public Integer getBoxId() {
        return boxId;
    }

    public void setBoxId(Integer boxId) {
        this.boxId = boxId;
    }

    public Timestamp getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(Timestamp pickupTime) {
        this.pickupTime = pickupTime;
    }

    public Integer getPickupUser() {
        return pickupUser;
    }

    public void setPickupUser(Integer pickupUser) {
        this.pickupUser = pickupUser;
    }

    public Integer getPickupType() {
        return pickupType;
    }

    public void setPickupType(Integer pickupType) {
        this.pickupType = pickupType;
    }

    private Integer deliveryType;
    private Integer boxId;
    private Timestamp pickupTime;
    private Integer pickupUser;
    private Integer pickupType;
}
