package com.zy.zmail.server.cabinet.service;

import com.zy.zmail.server.cabinet.entity.BoxInfo;
import com.zy.zmail.server.cabinet.entity.CabinetNode;

import java.util.List;

/**
 * Created by wenliz on 2017/2/10.
 */
public interface BoxService {
    public Integer save(BoxInfo boxInfo);

    public BoxInfo getById(Integer boxId);
    public List<BoxInfo> listByCabinetId(Integer cabinetId);
    public List<BoxInfo> listAvailabe(Integer cabinetId);
    public List<BoxInfo> listApplyMail(Integer cabinetId);
    public List<CabinetNode> cabinetAvailableCount(String boxTypes);

    public void deleteByCabinetId(Integer cabinetId);

}
