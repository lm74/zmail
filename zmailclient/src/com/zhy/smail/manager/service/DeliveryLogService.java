package com.zhy.smail.manager.service;

import com.zhy.smail.cabinet.entity.CabinetInfo;
import com.zhy.smail.cabinet.entity.CabinetNode;
import com.zhy.smail.config.GlobalOption;
import com.zhy.smail.manager.entity.DeliveryLog;
import com.zhy.smail.restful.*;

import java.util.List;

/**
 * Created by wenliz on 2017/2/14.
 */
public class DeliveryLogService {
    public static void listByCabinetId(Integer cabinetId, Integer periodType, Integer pickedup, RestfulResult result){
        String url = GlobalOption.getServerUrl() +"/deliveryLog/byCabinetId?cabinetId=" + cabinetId+
                "&periodType="+periodType+"&pickedup=" + pickedup;
        HttpOperator.get(url,getDefaultResultResult(result));
    }

    public static void listByOwner(Integer cabinetId, Integer ownerId,  Integer packuped, RestfulResult result){
        String url = GlobalOption.getServerUrl() +"/deliveryLog/byOwner?cabinetId=" + cabinetId+"&ownerId="+ownerId+"&pickuped="+packuped;
        HttpOperator.get(url,getDefaultResultResult(result));
    }

    public static void listAllByOwner(Integer ownerId, RestfulResult result){
        String url = GlobalOption.getServerUrl() +"/deliveryLog/allByOwner?ownerId="+ownerId+"&pickuped=0";
        HttpOperator.get(url,new DefaultRestfulResult(result){
            protected Object changeToObject(Object original){
                return JsonOperator.toObject(original, CabinetNode.class);
            }
        });
    }

    public static void listByDelivery(Integer cabinetId, Integer deliveryMan, Integer pickuped, Integer periodType, RestfulResult result){
        String url = GlobalOption.getServerUrl() +"/deliveryLog/byDelivery?cabinetId=" + cabinetId+"&deliveryMan=" + deliveryMan+
                "&pickuped="+pickuped+"&periodType="+periodType;
        HttpOperator.get(url,getDefaultResultResult(result));
    }

    public static void putdown(Integer deliveryMan, Integer ownerId, Integer boxId, Integer deliveryType, RestfulResult result){
        String url = GlobalOption.getServerUrl() +"/deliveryLog/putdown?deliveryMan=" + deliveryMan+"&ownerId="+ownerId+
                "&boxId=" + boxId+"&deliveryType=" + deliveryType;
        HttpOperator.get(url, new DefaultRestfulResult(result));
    }
    public static void pickup(Integer logId, Integer pickupUser, Integer pickupType, RestfulResult result){
        String url = GlobalOption.getServerUrl() +"/deliveryLog/pickup?logId=" + logId+"&pickupUser="+pickupUser+
                "&pickupType=" + pickupType;
        HttpOperator.get(url, new DefaultRestfulResult(result));
    }

    private static DefaultRestfulResult getDefaultResultResult(RestfulResult result){
        return new DefaultRestfulResult(result){
            protected Object changeToObject(Object original){
                return JsonOperator.toObject(original, DeliveryLog.class);
            }
        };
    }
}
