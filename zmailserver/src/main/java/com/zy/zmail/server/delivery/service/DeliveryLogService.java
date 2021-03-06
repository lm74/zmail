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
    public List<DeliveryLog> listByCabinetId(Integer cabinetId, Integer pickedup);
    public List<DeliveryLog> listByCabinetId(Integer cabinetId, Integer periodType, Integer pickedup);
    public List<DeliveryLog> listByOwner(Integer cabinetId, Integer ownerId, boolean pickuped);
    public List<DeliveryLog> listByDelivery(Integer cabinetId, Integer deliveryMan, Integer pickuped, Integer periodType);
    public List<CabinetNode> listAllByOwner(Integer ownerId, Integer pickuped);
    public List<DeliveryLog> listByOwner(Integer ownerId);
    public List<LogBrief> listByBoxId(Integer boxId);


    public Integer save(LogBrief brief);
    public LogBrief getBriefByLogId(Integer logId);
    public Integer deleteByCabinetId(Integer cabinetId);
}
