package com.zy.zmail.server.delivery.service;

import com.zy.zmail.server.cabinet.entity.CabinetNode;
import com.zy.zmail.server.common.json.JsonResult;
import com.zy.zmail.server.delivery.entity.DeliveryLog;
import com.zy.zmail.server.delivery.entity.LogBrief;

import java.util.List;

/**
 * Created by wenliz on 2017/2/14.
 */
public interface DeliveryLogService {
    public List<DeliveryLog> listByCabinetId(Integer cabinetId);
    public List<DeliveryLog> listByCabinetId(Integer cabinetId, Integer periodType);
    public List<DeliveryLog> listByOwner(Integer cabinetId, Integer ownerId, boolean pickuped);
    public List<DeliveryLog> listByDelivery(Integer cabinetId, Integer deliveryMan, Integer pickuped, Integer periodType);
    public List<CabinetNode> listAllByOwner(Integer ownerId, Integer pickuped);
    public List<DeliveryLog> listByOwner(Integer ownerId);


    public Integer save(LogBrief brief);
    public LogBrief getBriefByLogId(Integer logId);
}
