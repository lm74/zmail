package com.zhy.smail.manager.service;

import com.zhy.smail.config.GlobalOption;
import com.zhy.smail.manager.entity.DeliveryLog;
import com.zhy.smail.manager.entity.OpeningLog;
import com.zhy.smail.restful.DefaultRestfulResult;
import com.zhy.smail.restful.HttpOperator;
import com.zhy.smail.restful.JsonOperator;
import com.zhy.smail.restful.RestfulResult;

/**
 * Created by wenliz on 2017/3/2.
 */
public class OpeningLogService {
    public static void listByCabinetId(Integer cabinetId, Integer periodType, RestfulResult result){
        String url = GlobalOption.getServerUrl() +"/openingLog/byCabinetId?cabinetId=" + cabinetId+"&periodType="+periodType;
        HttpOperator.get(url,getDefaultResultResult(result));
    }

    public static void save(Integer openingUser, Integer boxId, String openingResult, RestfulResult result){
        String url = GlobalOption.getServerUrl() +"/openingLog/save?openingUser=" + openingUser+
                "&boxId=" + boxId+"&openingResult=" + openingResult;
        HttpOperator.get(url, new DefaultRestfulResult(result));
    }

    private static DefaultRestfulResult getDefaultResultResult(RestfulResult result){
        return new DefaultRestfulResult(result){
            protected Object changeToObject(Object original){
                return JsonOperator.toObject(original, OpeningLog.class);
            }
        };
    }
}
