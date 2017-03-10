package com.zhy.smail.user.service;

import com.zhy.smail.cabinet.entity.CabinetInfo;
import com.zhy.smail.config.GlobalOption;
import com.zhy.smail.config.LocalConfig;
import com.zhy.smail.restful.*;
import com.zhy.smail.user.entity.UserInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by wenliz on 2017/1/22.
 */
public class UserService {
    private static Log log = LogFactory.getLog(UserService.class);

    public static void testConnection(RestfulResult result){
        HttpOperator.get(GlobalOption.getServerUrl()+"/user/test", result);
    }

    public static void login(String userName, String password, RestfulResult result){
        String url = GlobalOption.getServerUrl() +"/user/login?userName=" + userName+"&password=" + password;
        HttpOperator.get(url, new RestfulResult() {
            @Override
            public void doResult(RfResultEvent event) {
                if(event.getData()!=null){
                    event.setData(JsonOperator.toObject(event.getData(), UserInfo.class));
                }
                result.doResult(event);
            }

            @Override
            public void doFault(RfFaultEvent event) {
                result.doFault(event);
            }
        });
    }

    public static void save(UserInfo userInfo, RestfulResult result){
        String url = GlobalOption.getServerUrl() +"/user";
        try {
            String value = HttpOperator.mapper.writeValueAsString(userInfo);
            log.info(value);
            HttpOperator.post(value, url, new RestfulResult() {
                @Override
                public void doResult(RfResultEvent event) {
                    if (event.getData() != null) {
                        event.setData(JsonOperator.toObject(event.getData(), UserInfo.class));
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

    public static void getByCardNo(String cardNo, RestfulResult result){
        String url = GlobalOption.getServerUrl() +"/user/byCardNo?cardNo=" + cardNo;
        HttpOperator.get(url, new RestfulResult() {
            @Override
            public void doResult(RfResultEvent event) {

                if((event.getResult()== RfResultEvent.OK) && (event.getData()!=null)){
                    event.setData(JsonOperator.toObject(event.getData(), UserInfo.class));
                }
                result.doResult(event);
            }


            @Override
            public void doFault(RfFaultEvent event) {
                result.doFault(event);
            }
        });
    }

    public static void listByUserTyeps(String userTypes, RestfulResult result){
        String url = GlobalOption.getServerUrl() +"/user/byUserTypes?userTypes=" + userTypes;
        HttpOperator.get(url, new RestfulResult() {
            @Override
            public void doResult(RfResultEvent event) {
                objectToUserInfo(event);
                result.doResult(event);
            }

            @Override
            public void doFault(RfFaultEvent event) {
                result.doFault(event);
            }
        });
    }

    protected static  void objectToUserInfo(RfResultEvent event) {
        if(event.getData()!=null){
            List users = (List)event.getData();
            for(int i=0; i<users.size();i++){
                users.set(i, JsonOperator.toObject(users.get(i), UserInfo.class));
            }
        }

    }

    public static void changePassword(Integer userId, String oldPassword, String newPassword, RestfulResult result){
        String url = GlobalOption.getServerUrl()+"/user/changePassword?userId=" + userId.toString()+"&oldPassword=" +
                oldPassword+"&newPassword=" + newPassword;
        HttpOperator.get(url, result);
    }

    public static void listAll(RestfulResult result){
        String url = GlobalOption.getServerUrl() +"/user";
        HttpOperator.get(url, new RestfulResult() {
            @Override
            public void doResult(RfResultEvent event) {
                objectToUserInfo(event);
                result.doResult(event);
            }

            @Override
            public void doFault(RfFaultEvent event) {
                result.doFault(event);
            }
        });
    }

    public static void listOwner(String filter, RestfulResult result){
        String url = GlobalOption.getServerUrl() +"/user/owner?filter="+filter;
        HttpOperator.get(url, new RestfulResult() {
            @Override
            public void doResult(RfResultEvent event) {
                objectToUserInfo(event);
                result.doResult(event);
            }

            @Override
            public void doFault(RfFaultEvent event) {
                result.doFault(event);
            }
        });
    }

    public static void listOwnerByRoom(String buildingNo, String unitNo,String floorNo, String roomNo, RestfulResult result){
        String url = GlobalOption.getServerUrl() +"/user/owner/byRoom?buildingNo="+buildingNo+"&unitNo="+unitNo+
                "&floorNo="+floorNo+"&roomNo="+roomNo;
        HttpOperator.get(url, new RestfulResult() {
            @Override
            public void doResult(RfResultEvent event) {
                objectToUserInfo(event);
                result.doResult(event);
            }

            @Override
            public void doFault(RfFaultEvent event) {
                result.doFault(event);
            }
        });
    }

    public static void delete(Integer userId, RestfulResult result){
        String url = GlobalOption.getServerUrl() +"/user/" + userId.toString();
        HttpOperator.delete(url, result);
    }

    public static void deleteByIds(String ids, RestfulResult result){
        String url = GlobalOption.getServerUrl() +"/user/deleteByIds";
        try {
            HttpOperator.post(ids, url,result);
        }
        catch (Exception e){

        }
    }
}
