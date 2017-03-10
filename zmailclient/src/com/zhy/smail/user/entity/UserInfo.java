package com.zhy.smail.user.entity;

import com.zhy.smail.common.utils.MapUtils;
import com.zhy.smail.component.SimpleDialog;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import sun.java2d.pipe.SpanShapeRenderer;

import java.util.Map;

/**
 * Created by wenliz on 2017/1/22.
 */
public class UserInfo {
    public final static int FACTORY_USER = 10;
    public final static int ADVANCED_ADMIN = 11;
    public final static int ADMIN = 12;
    public final static int DELIVERY = 20;
    public final static int MAILMAN = 21;
    public final static int OWNER = 30;


    private SimpleBooleanProperty checked;
    private Integer userId;
    private String userName;
    private SimpleStringProperty userNameProperty;
    private Integer userType;
    private String password;
    private String phoneNo;
    private SimpleStringProperty phoneNoProperty;

    public UserInfo(){
        this.userType = OWNER;
        this.userId = -1;
        this.password = "123456";
        checked = new SimpleBooleanProperty(false);
    }

    public boolean isChecked() {
        return checked.get();
    }

    public void setChecked(boolean checked) {
        this.checked.set(checked);
    }

    public SimpleBooleanProperty checkedProperty(){
        return checked;
    }


    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
        if(phoneNo == null){
            phoneNoProperty = new SimpleStringProperty(this, "phoneNo", "");
        }
        else {
            phoneNoProperty = new SimpleStringProperty(this, "phoneNo", this.phoneNo);
        }
    }

    public ReadOnlyStringProperty phoneNoProperty(){
        return phoneNoProperty;
    }

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
        if(userName == null){
            this.userNameProperty = new SimpleStringProperty(this, "userName", "");
        }
        else {
            this.userNameProperty = new SimpleStringProperty(this, "userName", this.userName);
        }
    }

    public ReadOnlyStringProperty userNameProperty(){
        return this.userNameProperty;
    }

    public Integer getUserType() {
        return userType;
    }
    public String getUserTypeTitle(){
        if(userType == OWNER){
            return "业主";
        }
        else if(userType == DELIVERY){
            return "投递员";
        }
        else if(userType == ADMIN){
            return "管理员";
        }
        else if(userType == ADVANCED_ADMIN){
            return "高级管理员";
        }
        else if(userType == FACTORY_USER){
            return "厂家管理员";
        } else{
            return "";
        }
    }
    public Integer getUserType(String typeName){
        if(typeName.equals("业主")){
            return OWNER;
        }
        else if(typeName.equals("投递员")){
            return DELIVERY;
        }
        else if(typeName.equals("管理员")){
            return ADMIN;
        }
        else if(typeName.equals("高级管理员")){
            return ADVANCED_ADMIN;
        }
        else if(typeName.equals("厂家管理员")){
            return FACTORY_USER;
        }
        else {
            return null;
        }
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
        if(buildingNo == null){
            this.buildingNoProperty = new SimpleStringProperty(this, "buildingNo", "");
        }
        else{
            this.buildingNoProperty = new SimpleStringProperty(this, "buildingNo", this.buildingNo);
        }
    }

    public ReadOnlyStringProperty buildingNoProperty(){
        return this.buildingNoProperty;
    }

    public String getUnitNo() {
        return unitNo;
    }

    public void setUnitNo(String unitNo) {
        this.unitNo = unitNo;
        if(unitNo == null){
            this.unitNoProperty = new SimpleStringProperty(this, "unitNo", "");
        }
        else{
            this.unitNoProperty = new SimpleStringProperty(this, "unitNo", this.unitNo);
        }
    }

    public ReadOnlyStringProperty unitNoProperty(){
        return this.unitNoProperty;
    }


    public String getFloorNo() {
        return floorNo;
    }

    public void setFloorNo(String floorNo) {
        this.floorNo = floorNo;
        if(floorNo == null){
            this.floorNoProperty = new SimpleStringProperty(this, "floorNo", "");
        }
        else{
            this.floorNoProperty = new SimpleStringProperty(this, "floorNo", this.floorNo);
        }
    }

    public ReadOnlyStringProperty floorNoProperty(){
        return this.floorNoProperty;
    }


    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
        if(roomNo == null){
            this.roomNoProperty = new SimpleStringProperty(this, "roomNo", "");
        }
        else{
            this.roomNoProperty = new SimpleStringProperty(this, "roomNo", this.roomNo);
        }
    }

    public ReadOnlyStringProperty roomNoProperty(){
        return this.roomNoProperty;
    }

    public String getCardNo1() {
        return cardNo1;
    }

    public void setCardNo1(String cardNo1) {
        this.cardNo1 = cardNo1;
        if(cardNo1==null){
            this.cardNo1Property = new SimpleStringProperty(this, "cardNo1", "");
        }
        else{
            this.cardNo1Property = new SimpleStringProperty(this, "cardNo1", this.cardNo1);
        }
    }

    public ReadOnlyStringProperty cardNo1Property(){
        return this.cardNo1Property;
    }

    public String getCardNo2() {
        return cardNo2;
    }

    public void setCardNo2(String cardNo2) {
        this.cardNo2 = cardNo2;
        if(cardNo2==null){
            this.cardNo2Property = new SimpleStringProperty(this, "cardNo2", "");
        }
        else{
            this.cardNo2Property = new SimpleStringProperty(this, "cardNo2", this.cardNo2);
        }
    }

    public ReadOnlyStringProperty cardNo2Property(){
        return this.cardNo2Property;
    }

    public String getCardNo3() {
        return cardNo3;
    }

    public void setCardNo3(String cardNo3) {
        this.cardNo3 = cardNo3;
        if(cardNo3==null){
            this.cardNo3Property = new SimpleStringProperty(this, "cardNo3", "");
        }
        else{
            this.cardNo3Property = new SimpleStringProperty(this, "cardNo3", this.cardNo3);
        }
    }

    public ReadOnlyStringProperty cardNo3Property(){
        return this.cardNo3Property;
    }

    public String getCardNo4() {
        return cardNo4;
    }

    public void setCardNo4(String cardNo4) {
        this.cardNo4 = cardNo4;
        if(cardNo4==null){
            this.cardNo4Property = new SimpleStringProperty(this, "cardNo4", "");
        }
        else{
            this.cardNo4Property = new SimpleStringProperty(this, "cardNo4", this.cardNo4);
        }
    }

    public ReadOnlyStringProperty cardNo4Property(){
        return this.cardNo4Property;
    }

    public String getCardNo5() {
        return cardNo5;
    }

    public void setCardNo5(String cardNo5) {
        this.cardNo5 = cardNo5;
        if(cardNo5==null){
            this.cardNo5Property = new SimpleStringProperty(this, "cardNo5", "");
        }
        else{
            this.cardNo5Property = new SimpleStringProperty(this, "cardNo5", this.cardNo5);
        }
    }

    public ReadOnlyStringProperty cardNo5Property(){
        return this.cardNo5Property;
    }

    public String getCardNo6() {
        return cardNo6;
    }

    public void setCardNo6(String cardNo6) {
        this.cardNo6 = cardNo6;
        if(cardNo6==null){
            this.cardNo6Property = new SimpleStringProperty(this, "cardNo6", "");
        }
        else{
            this.cardNo6Property = new SimpleStringProperty(this, "cardNo6", this.cardNo6);
        }
    }

    public ReadOnlyStringProperty cardNo6Property(){
        return this.cardNo6Property;
    }

    public String getCardNo7() {
        return cardNo7;
    }

    public void setCardNo7(String cardNo7) {
        this.cardNo7 = cardNo7;
        if(cardNo7==null){
            this.cardNo7Property = new SimpleStringProperty(this, "cardNo7", "");
        }
        else{
            this.cardNo7Property = new SimpleStringProperty(this, "cardNo7", this.cardNo7);
        }
    }

    public ReadOnlyStringProperty cardNo7Property(){
        return this.cardNo7Property;
    }

    public String getCardNo8() {
        return cardNo8;
    }

    public void setCardNo8(String cardNo8) {
        this.cardNo8 = cardNo8;
        if(cardNo8==null){
            this.cardNo8Property = new SimpleStringProperty(this, "cardNo8", "");
        }
        else{
            this.cardNo8Property = new SimpleStringProperty(this, "cardNo8", this.cardNo8);
        }
    }

    public ReadOnlyStringProperty cardNo8Property(){
        return this.cardNo8Property;
    }

    public String getCardNo9() {
        return cardNo9;
    }

    public void setCardNo9(String cardNo9) {
        this.cardNo9 = cardNo9;
        if(cardNo9==null){
            this.cardNo9Property = new SimpleStringProperty(this, "cardNo9", "");
        }
        else{
            this.cardNo9Property = new SimpleStringProperty(this, "cardNo9", this.cardNo9);
        }
    }

    public ReadOnlyStringProperty cardNo9Property(){
        return this.cardNo9Property;
    }

    public String getCardNo10() {
        return cardNo10;
    }

    public void setCardNo10(String cardNo10) {
        this.cardNo10 = cardNo10;
        if(cardNo10==null){
            this.cardNo10Property = new SimpleStringProperty(this, "cardNo10", "");
        }
        else{
            this.cardNo10Property = new SimpleStringProperty(this, "cardNo10", this.cardNo10);
        }
    }

    public ReadOnlyStringProperty cardNo10Property(){
        return this.cardNo10Property;
    }



    private String buildingNo;
    private SimpleStringProperty buildingNoProperty;
    private String unitNo;
    private SimpleStringProperty unitNoProperty;
    private String floorNo;
    private SimpleStringProperty floorNoProperty;
    private String roomNo;
    private SimpleStringProperty roomNoProperty;
    private String cardNo1;
    private SimpleStringProperty cardNo1Property;
    private String cardNo2;
    private SimpleStringProperty cardNo2Property;
    private String cardNo3;
    private SimpleStringProperty cardNo3Property;
    private String cardNo4;
    private SimpleStringProperty cardNo4Property;
    private String cardNo5;
    private SimpleStringProperty cardNo5Property;
    private String cardNo6;
    private SimpleStringProperty cardNo6Property;
    private String cardNo7;
    private SimpleStringProperty cardNo7Property;
    private String cardNo8;
    private SimpleStringProperty cardNo8Property;
    private String cardNo9;
    private SimpleStringProperty cardNo9Property;
    private String cardNo10;
    private SimpleStringProperty cardNo10Property;

    public String toString(){
        return this.userName;
    }
}
