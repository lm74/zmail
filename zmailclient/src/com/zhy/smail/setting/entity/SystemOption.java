package com.zhy.smail.setting.entity;

import com.zhy.smail.common.utils.MapUtils;

import java.sql.Timestamp;
import java.util.Map;

/**
 * Created by wenliz on 2017/1/23.
 */
public class SystemOption {
    public static final int APP_TITLE_ID = 150;
    public static final int DOOR_SERVER_IP_ID = 100;
    public static final int DOOR_SERVER_PORT_ID = 101;
    public static final int DOOR_PROTOCOL_ID = 102;

    public static final int TIMEOUT_ID = 120;
    public static final int BUILDING_NO_ID = 121;
    public static final int UNIT_NO_ID = 122;
    public static final int USE_DAYS_ID = 125;
    public static final int USE_START_ID = 126;
    public static final int MAIN_TITLE_ID = 150;
    public static final int NO_NEED_PASSWORD_ID = 200;
    public static final int DELIVERY_SAME_MAIL_ID = 201;
    public static final int REMAIN_TIME_ID = 210;



    private Integer optionId;
    private String optionName;
    private String valueType;
    private String charValue;
    private Integer intValue;
    private Timestamp dateValue;

    public Integer getOptionId() {
        return optionId;
    }

    public void setOptionId(Integer optionId) {
        this.optionId = optionId;
    }

    public String getOptionName() {
        return optionName;
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public String getCharValue() {
        return charValue;
    }

    public void setCharValue(String charValue) {
        this.charValue = charValue;
    }

    public Integer getIntValue() {
        return intValue;
    }

    public void setIntValue(Integer intValue) {
        this.intValue = intValue;
    }

    public Timestamp getDateValue() {
        return dateValue;
    }

    public void setDateValue(Timestamp dateValue) {
        this.dateValue = dateValue;
    }

    public static SystemOption parse(Map other){
        SystemOption option = new SystemOption();
        option.setOptionId(MapUtils.getInteger(other, "optionId"));
        option.setOptionName(MapUtils.getString(other, "optionName"));
        option.setValueType(MapUtils.getString(other, "valueType"));
        option.setCharValue(MapUtils.getString(other, "charValue"));
        option.setIntValue(MapUtils.getInteger(other, "intValue"));
        option.setDateValue(MapUtils.getTimeStamp(other, "dateValue"));
        return option;
    }
}
