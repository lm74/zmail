package com.zy.zmail.server.setting.service;

import com.zy.zmail.server.setting.entity.SystemOption;

import java.util.List;

/**
 * Created by wenliz on 2017/1/23.
 */
public interface OptionService {
    public SystemOption getById(Integer optionId);
    public List<SystemOption> listAll();
    public Integer save(SystemOption option);
}
