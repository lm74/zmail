package com.zy.zmail.server.user.service;

import com.zy.zmail.server.user.entity.UserInfo;

import java.util.List;

/**
 * Created by wenliz on 2017/1/22.
 */
public interface UserService {
    public UserInfo getByUserName(String userName);
    public UserInfo getByUserName(String userName, Integer excludeUserId);
    public UserInfo getByPhoneNo(String phoneNo);
    public UserInfo getByPhoneNo(String phoneNo, Integer excludeUserId);
    public UserInfo getByCardNo(String cardNo);
    public UserInfo getByCardNo(String cardNo, Integer excludeUserId);

    public UserInfo getByUserId(Integer userId);

    public List<UserInfo> listByUserTypes(String userTypes);
    public List<UserInfo> listAll();
    public List<UserInfo> listOwner(String filter);
    public List<UserInfo> listOwnerByRoom(String buildingNo, String unitNo, String floorNo, String roomNo);

    public Integer save(UserInfo userInfo);
    public Integer changePassword(Integer userId, String oldPassword, String newPassword);

    public void delete(Integer userId);
    public void deleteByIds(String ids);

    public List<UserInfo> findUserInfoByNoPickupMail();
}
