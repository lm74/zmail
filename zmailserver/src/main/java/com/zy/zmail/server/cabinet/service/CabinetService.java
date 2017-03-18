package com.zy.zmail.server.cabinet.service;

import com.zy.zmail.server.cabinet.entity.CabinetInfo;

import java.util.List;

/**
 *
 * Created by wenliz on 2017/2/8.
 */
public interface CabinetService {
    public List<CabinetInfo> listAll();
    public Integer save(CabinetInfo cabinetInfo);
    public void delete(Integer cabinetId);
    public CabinetInfo getByCabinetNo(String cabinetNo);
    public Integer sameCabinetNo(String cabinetNo);
    public CabinetInfo getByCabinetId(Integer cabnietId);
}
