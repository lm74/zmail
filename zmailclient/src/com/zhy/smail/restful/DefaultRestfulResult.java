package com.zhy.smail.restful;

import com.zhy.smail.cabinet.entity.BoxInfo;

import java.util.List;
import java.util.Objects;

/**
 * Created by wenliz on 2017/2/28.
 */
public class DefaultRestfulResult implements RestfulResult {
    private RestfulResult result;
    public DefaultRestfulResult(RestfulResult result){
        this.result = result;
    }

    public DefaultRestfulResult(){
        this.result = null;
    }

    public void doResult(RfResultEvent event){
        if(event.getResult() == RfResultEvent.OK){
            if(event.getData()!=null){
                Object data = event.getData();
                if(data instanceof List){
                    List dataList = (List)data;
                    for(int i=0; i<dataList.size(); i++){
                        dataList.set(i, changeToObject(dataList.get(i)));
                    }
                }
                else{
                    event.setData(changeToObject( event.getData()));
                }
            }
        }
        if(result != null) {
            result.doResult(event);
        }
    }
    public void doFault(RfFaultEvent event){
        if(result!= null) {
            result.doFault(event);
        }
    }

    protected Object changeToObject(Object original){
        return original;
    }
}
