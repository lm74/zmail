package com.zhy.smail.cabinet.service;

import com.zhy.smail.cabinet.entity.CabinetInfo;
import com.zhy.smail.config.GlobalOption;
import com.zhy.smail.restful.*;
import com.zhy.smail.user.entity.UserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenliz on 2017/2/8.
 */
public class CabinetService {
    public static void listAll(RestfulResult result){
        String url = GlobalOption.getServerUrl() +"/cabinet";
        HttpOperator.get(url, new RestfulResult() {
            @Override
            public void doResult(RfResultEvent event) {
                if(event.getData()!=null){
                    List cabinets = (List)event.getData();
                    for(int i=0; i<cabinets.size();i++){
                        cabinets.set(i,JsonOperator.toObject(cabinets.get(i), CabinetInfo.class));
                    }
                }
                result.doResult(event);
            }

            @Override
            public void doFault(RfFaultEvent event) {
                result.doFault(event);
            }
        });
    }

    public static void getByCabinetNo(String cabinetNo, RestfulResult result){
        String url = GlobalOption.getServerUrl() +"/cabinet/byCabinetNo?cabinetNo="+cabinetNo;
        HttpOperator.get(url, new RestfulResult() {
            @Override
            public void doResult(RfResultEvent event) {
                if(event.getData()!=null){
                    event.setData(JsonOperator.toObject(event.getData(), CabinetInfo.class));
                }
                result.doResult(event);
            }

            @Override
            public void doFault(RfFaultEvent event) {
                result.doFault(event);
            }
        });
    }

    public static void save(CabinetInfo cabinet, RestfulResult result){
        String url = GlobalOption.getServerUrl() +"/cabinet";
        try {
            String value = HttpOperator.mapper.writeValueAsString(cabinet);
            HttpOperator.post(value, url, new RestfulResult() {
                @Override
                public void doResult(RfResultEvent event) {
                    if (event.getData() != null) {
                        event.setData(JsonOperator.toObject(event.getData(), CabinetInfo.class));
                    }
                    result.doResult(event);
                }

                @Override
                public void doFault(RfFaultEvent event) {
                    result.doFault(event);
                }
            });
        }
        catch (Exception e){

        }
    }

    public static void delete(Integer cabinetId, RestfulResult result){
        String url = GlobalOption.getServerUrl() +"/cabinet/" + cabinetId.toString();
        HttpOperator.delete(url, result);
    }
}
