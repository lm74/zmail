package com.zhy.smail.setting.service;

import com.zhy.smail.config.GlobalOption;
import com.zhy.smail.restful.*;
import com.zhy.smail.setting.entity.SystemOption;

import java.util.List;

/**
 * Created by wenliz on 2017/1/23.
 */
public class OptionService {

    public static void getById(Integer optionId, RestfulResult result){
        String url = GlobalOption.getServerUrl() +"/setting/option/byId?optionId=" + optionId;
        HttpOperator.get(url, new RestfulResult() {
            @Override
            public void doResult(RfResultEvent event) {
                if(event.getData()!=null){
                    event.setData(JsonOperator.toObject(event.getData(), SystemOption.class));
                }
                result.doResult(event);
            }

            @Override
            public void doFault(RfFaultEvent event) {
                result.doFault(event);
            }
        });
    }

    public static void listAll(RestfulResult result){
        String url = GlobalOption.getServerUrl() +"/setting/option" ;
        HttpOperator.get(url, new RestfulResult() {
            @Override
            public void doResult(RfResultEvent event) {
                if(event.getData()!=null){
                    List options = (List)event.getData();
                    for(int i=0; i<options.size();i++){
                        options.set(i,JsonOperator.toObject(options.get(i), SystemOption.class));
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

    public static void loadOptions(RestfulResult result){
        OptionService.listAll(new RestfulResult() {
            @Override
            public void doResult(RfResultEvent event) {
                if(event.getResult() != RfResultEvent.OK){
                    result.doResult(event);
                    return;
                }
                if(event.getData()!=null) {
                    List<SystemOption> options = (List<SystemOption>) event.getData();

                    for(int i=0; i<options.size(); i++){
                        SystemOption option = options.get(i);
                        switch (option.getOptionId()){
                            case SystemOption.TIMEOUT_ID:
                                GlobalOption.timeout = option;
                                break;
                            case SystemOption.MAIN_TITLE_ID:
                                GlobalOption.mainTitle = option;
                                break;
                            case SystemOption.DOOR_SERVER_IP_ID:
                                GlobalOption.doorServerIp = option;
                                break;
                            case SystemOption.DOOR_SERVER_PORT_ID:
                                GlobalOption.doorServerPort = option;
                                break;
                            case SystemOption.NO_NEED_PASSWORD_ID:
                                GlobalOption.cardNeedPassword = option;
                                break;
                            case SystemOption.DELIVERY_SAME_MAIL_ID:
                                GlobalOption.deliverySameMail = option;
                                break;
                            case SystemOption.DOOR_PROTOCOL_ID:
                                GlobalOption.doorProtocol = option;
                                break;
                            case SystemOption.BUILDING_NO_ID:
                                GlobalOption.buildingNo = option;
                                break;
                            case SystemOption.UNIT_NO_ID:
                                GlobalOption.unitNo = option;
                                break;
                            case SystemOption.USE_DAYS_ID:
                                GlobalOption.useDays = option;
                                break;
                            case SystemOption.USE_START_ID:
                                GlobalOption.useStart = option;
                                break;
                            case SystemOption.REMAIN_TIME_ID:
                                GlobalOption.remainTime = option;
                                break;

                        }

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

    public static void save(SystemOption option, RestfulResult result){
        String url = GlobalOption.getServerUrl() +"/setting/option";
        try {
            String value = HttpOperator.mapper.writeValueAsString(option);
            HttpOperator.post(value, url, new RestfulResult() {
                @Override
                public void doResult(RfResultEvent event) {
                    if (event.getData() != null) {
                        event.setData(JsonOperator.toObject(event.getData(), SystemOption.class));
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

    public static void testDoorSystem(Integer protocolType, String serverIp, Integer portNo, RestfulResult result){
        String url = GlobalOption.getServerUrl() +"/setting/option/testDoorSystem?protocolType="+protocolType+
                "&serverIp=" + serverIp+"&serverPort="+portNo;
        HttpOperator.get(url,result);
    }
}
