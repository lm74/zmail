package com.zy.zmail.server.delivery.service;

import com.zy.zmail.server.cabinet.entity.CabinetNode;
import com.zy.zmail.server.delivery.entity.DeliveryLog;
import com.zy.zmail.server.delivery.entity.LogBrief;
import com.zy.zmail.server.delivery.entity.OpeningBrief;
import com.zy.zmail.server.delivery.entity.OpeningLog;

import java.util.List;

/**
 * Created by wenliz on 2017/3/2.
 */
public interface OpeningLogService {
    public List<OpeningLog> listByCabinetId(Integer cabinetId);
    public List<OpeningLog> listByCabinetId(Integer cabinetId, Integer periodType);
    public Integer save(OpeningBrief brief);
    public OpeningBrief getBriefByLogId(Integer logId);
}
