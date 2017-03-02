package com.zhy.smail.cabinet.service;

import com.zhy.smail.cabinet.entity.BoxInfo;
import com.zhy.smail.cabinet.entity.CabinetInfo;
import com.zhy.smail.config.GlobalOption;
import com.zhy.smail.restful.*;

import java.util.List;

/**
 * Created by wenliz on 2017/2/11.
 */
public class BoxService {
    public static void listByCabinetId(Integer cabinetId, RestfulResult result){
        String url = GlobalOption.getServerUrl() +"/box/byCabinetId?cabinetId="+cabinetId.toString();
        HttpOperator.get(url, getBoxResultResult(result));
    }

    public static void listApplyMail(Integer cabinetId, RestfulResult result){
        String url = GlobalOption.getServerUrl() +"/box/applyMail?cabinetId="+cabinetId.toString();
        HttpOperator.get(url, getBoxResultResult(result));
    }



    public static void save(BoxInfo boxInfo, RestfulResult result){
        String url = GlobalOption.getServerUrl() +"/box";
        try {
            String value = HttpOperator.mapper.writeValueAsString(boxInfo);
            HttpOperator.post(value, url, getBoxResultResult(result));
        }
        catch (Exception e){

        }
    }

    public static void listAvailableBox(Integer cabinetId, RestfulResult result){
        String url = GlobalOption.getServerUrl() +"/box/available?cabinetId=" + cabinetId;
        HttpOperator.get(url, getBoxResultResult(result));
    }

    public static  void getAnotherMaxAvailableCabinet(Integer currentCabinetId, String boxTypes, RestfulResult result){
        String url = GlobalOption.getServerUrl() +"/box/anotherMaxAvailableCabinet?currentCabinetId=" + currentCabinetId+"&boxTypes="+boxTypes;
        HttpOperator.get(url, new DefaultRestfulResult(result));
    }

    public static DefaultRestfulResult getBoxResultResult(RestfulResult result){
        return new DefaultRestfulResult(result){
            @Override
            protected Object changeToObject(Object original){
                return JsonOperator.toObject(original, BoxInfo.class);
            }
        };
    }

}
