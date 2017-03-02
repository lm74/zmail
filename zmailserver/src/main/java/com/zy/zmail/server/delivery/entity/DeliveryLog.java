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
public class DeliveryLog  implements Serializable{
    @SequenceGenerator(name="sq_deliverylog", sequenceName = "sq_deliverylog", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_deliverylog")
    @Id
    private Integer logId;
    private Timestamp deliveryTime;

    @ManyToOne
    @JoinColumn(name="deliveryMan")
    private UserInfo deliveryMan;
    private Integer deliveryType;

    @ManyToOne
    @JoinColumn(name="boxId")
    private BoxInfo boxInfo;
    private Timestamp pickupTime;

    @ManyToOne
    @JoinColumn(name="pickupUser")
    private UserInfo pickupUser;
    private Integer pickupType;

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

    public UserInfo getDeliveryMan() {
        return deliveryMan;
    }

    public void setDeliveryMan(UserInfo deliveryMan) {
        this.deliveryMan = deliveryMan;
    }

    public Integer getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(Integer deliveryType) {
        this.deliveryType = deliveryType;
    }

    public BoxInfo getBoxInfo() {
        return boxInfo;
    }

    public void setBoxInfo(BoxInfo boxInfo) {
        this.boxInfo = boxInfo;
    }

    public Timestamp getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(Timestamp pickupTime) {
        this.pickupTime = pickupTime;
    }


    public UserInfo getPickupUser() {
        return pickupUser;
    }

    public void setPickupUser(UserInfo pickupUser) {
        this.pickupUser = pickupUser;
    }

    public Integer getPickupType() {
        return pickupType;
    }

    public void setPickupType(Integer pickupType) {
        this.pickupType = pickupType;
    }
}
