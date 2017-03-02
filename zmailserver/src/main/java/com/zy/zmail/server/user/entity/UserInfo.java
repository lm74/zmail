package com.zy.zmail.server.user.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by wenliz on 2017/1/22.
 */
@Entity
@Table(name="UserInfos")
public class UserInfo  implements Serializable{
    @SequenceGenerator(name="sq_userinfos", sequenceName = "sq_userinfos", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_userinfos")
    @Id
    private Integer userId;

    private String userName;
    private String phoneNo;

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    private Integer userType;
    private String password;
    private String buildingNo;
    private String unitNo;
    private String roomNo;
    private String cardNo1;
    private String cardNo2;
    private String cardNo3;
    private String cardNo4;
    private String cardNo5;
    private String cardNo6;
    private String cardNo7;
    private String cardNo8;
    private String cardNo9;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBuildingNo() {
        return buildingNo;
    }

    public void setBuildingNo(String buildingNo) {
        this.buildingNo = buildingNo;
    }

    public String getUnitNo() {
        return unitNo;
    }

    public void setUnitNo(String unitNo) {
        this.unitNo = unitNo;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public String getCardNo1() {
        return cardNo1;
    }

    public void setCardNo1(String cardNo1) {
        this.cardNo1 = cardNo1;
    }

    public String getCardNo2() {
        return cardNo2;
    }

    public void setCardNo2(String cardNo2) {
        this.cardNo2 = cardNo2;
    }

    public String getCardNo3() {
        return cardNo3;
    }

    public void setCardNo3(String cardNo3) {
        this.cardNo3 = cardNo3;
    }

    public String getCardNo4() {
        return cardNo4;
    }

    public void setCardNo4(String cardNo4) {
        this.cardNo4 = cardNo4;
    }

    public String getCardNo5() {
        return cardNo5;
    }

    public void setCardNo5(String cardNo5) {
        this.cardNo5 = cardNo5;
    }

    public String getCardNo6() {
        return cardNo6;
    }

    public void setCardNo6(String cardNo6) {
        this.cardNo6 = cardNo6;
    }

    public String getCardNo7() {
        return cardNo7;
    }

    public void setCardNo7(String cardNo7) {
        this.cardNo7 = cardNo7;
    }

    public String getCardNo8() {
        return cardNo8;
    }

    public void setCardNo8(String cardNo8) {
        this.cardNo8 = cardNo8;
    }

    public String getCardNo9() {
        return cardNo9;
    }

    public void setCardNo9(String cardNo9) {
        this.cardNo9 = cardNo9;
    }

    public String getCardNo10() {
        return cardNo10;
    }

    public void setCardNo10(String cardNo10) {
        this.cardNo10 = cardNo10;
    }

    private String cardNo10;
}
