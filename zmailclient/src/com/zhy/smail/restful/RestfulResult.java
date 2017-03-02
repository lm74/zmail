package com.zhy.smail.restful;

/**
 * Created by wenliz on 2017/1/22.
 */
public interface RestfulResult {
    public void doResult(RfResultEvent event);
    public void doFault(RfFaultEvent event);
}
