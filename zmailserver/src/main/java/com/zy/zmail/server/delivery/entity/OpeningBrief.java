package com.zy.zmail.server.delivery.entity;

import com.zy.zmail.server.cabinet.entity.BoxInfo;
import com.zy.zmail.server.user.entity.UserInfo;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by wenliz on 2017/3/2.
 */
@Entity
@Table(name="OpeningLog")
public class OpeningBrief implements Serializable {
    @SequenceGenerator(name="sq_openinglog", sequenceName = "sq_openinglog", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_openinglog")
    @Id
    private Integer logId;
    private Timestamp openingTime;
    private Integer openingUser;
    private Integer boxId;
    private String openingResult;

    public Integer getLogId() {
        return logId;
    }

    public void setLogId(Integer logId) {
        this.logId = logId;
    }

    public Timestamp getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(Timestamp openingTime) {
        this.openingTime = openingTime;
    }

    public Integer getOpeningUser() {
        return openingUser;
    }

    public void setOpeningUser(Integer openingUser) {
        this.openingUser = openingUser;
    }

    public Integer getBoxId() {
        return boxId;
    }

    public void setBoxId(Integer boxId) {
        this.boxId = boxId;
    }

    public String getOpeningResult() {
        return openingResult;
    }

    public void setOpeningResult(String openingResult) {
        this.openingResult = openingResult;
    }
}
