package com.zy.zmail.server.user.service;

import com.zy.zmail.server.user.entity.UserInfo;

import java.util.List;

/**
 * Created by wenliz on 2017/1/22.
 */
public interface UserService {
    public UserInfo getByUserName(String userName);
    public UserInfo getByPhoneNo(String phoneNo);
    public UserInfo getByCardNo(String cardNo);

    public List<UserInfo> listByUserTypes(String userTypes);
    public List<UserInfo> listAll();
    public List<UserInfo> listOwner(String filter);
    public List<UserInfo> listOwnerByRoom(String buildingNo, String unitNo, String roomNo);

    public Integer save(UserInfo userInfo);
    public Integer changePassword(Integer userId, String oldPassword, String newPassword);

    public void delete(Integer userId);
}
