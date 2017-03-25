package com.zy.zmail.server.user.service;

import com.zy.zmail.server.user.entity.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by wenliz on 2017/1/22.
 */
@Service("userService")
@Transactional
public class UserServiceImpl implements UserService{
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @PersistenceContext
    private EntityManager em;

    public UserInfo getByUserId(Integer userId){
        return em.find(UserInfo.class, userId);
    }

    public UserInfo getByUserName(String userName){
        Query query = em.createQuery("from UserInfo user where user.userName = :userName");
        query.setParameter("userName", userName);
        List users = query.getResultList();
        if(users.size()==0){
            return null;
        }
        else{
            return (UserInfo) users.get(0);
        }
    }

    public UserInfo getByUserName(String userName, Integer excludeUserId){
        Query query = em.createQuery("from UserInfo user where user.userName = :userName and user.userId<>:userId");
        query.setParameter("userName", userName);
        query.setParameter("userId", excludeUserId);
        List users = query.getResultList();
        if(users.size()==0){
            return null;
        }
        else{
            return (UserInfo) users.get(0);
        }
    }

    public UserInfo getByPhoneNo(String phoneNo){
        Query query = em.createQuery("from UserInfo user where user.phoneNo = :phoneNo");
        query.setParameter("phoneNo", phoneNo);
        List users = query.getResultList();
        if(users.size()==0){
            return null;
        }
        else{
            return (UserInfo) users.get(0);
        }
    }
    public UserInfo getByPhoneNo(String phoneNo, Integer excludeUserId){
        Query query = em.createQuery("from UserInfo user where user.phoneNo = :phoneNo and user.userId <>:userId");
        query.setParameter("phoneNo", phoneNo);
        query.setParameter("userId", excludeUserId);
        List users = query.getResultList();
        if(users.size()==0){
            return null;
        }
        else{
            return (UserInfo) users.get(0);
        }
    }
    public UserInfo getByCardNo(String cardNo){
        String jpql = "from UserInfo user " +
                "where user.cardNo1 = :cardNo1 or  " +
                "      user.cardNo2 = :cardNo2 or  " +
                "      user.cardNo3 = :cardNo3 or  " +
                "      user.cardNo4 = :cardNo4 or  " +
                "      user.cardNo5 = :cardNo5 or  " +
                "      user.cardNo6 = :cardNo6 or  " +
                "      user.cardNo7 = :cardNo7 or  " +
                "      user.cardNo8 = :cardNo8 or  " +
                "      user.cardNo9 = :cardNo9 or  " +
                "      user.cardNo10 = :cardNo10 ";
        Query query = em.createQuery(jpql);
        query.setParameter("cardNo1", cardNo);
        query.setParameter("cardNo2", cardNo);
        query.setParameter("cardNo3", cardNo);
        query.setParameter("cardNo4", cardNo);
        query.setParameter("cardNo5", cardNo);
        query.setParameter("cardNo6", cardNo);
        query.setParameter("cardNo7", cardNo);
        query.setParameter("cardNo8", cardNo);
        query.setParameter("cardNo9", cardNo);
        query.setParameter("cardNo10", cardNo);
        List<UserInfo> users = query.getResultList();
        if(users.size()>0){
            return users.get(0);
        }
        else {
            return null;
        }
    }

    public UserInfo getByCardNo(String cardNo, Integer excludeUserId){
        String jpql = "from UserInfo user " +
                "where (user.cardNo1 = :cardNo1 or  " +
                "      user.cardNo2 = :cardNo2 or  " +
                "      user.cardNo3 = :cardNo3 or  " +
                "      user.cardNo4 = :cardNo4 or  " +
                "      user.cardNo5 = :cardNo5 or  " +
                "      user.cardNo6 = :cardNo6 or  " +
                "      user.cardNo7 = :cardNo7 or  " +
                "      user.cardNo8 = :cardNo8 or  " +
                "      user.cardNo9 = :cardNo9 or  " +
                "      user.cardNo10 = :cardNo10 ) and user.userId <> :userId ";
        Query query = em.createQuery(jpql);
        query.setParameter("cardNo1", cardNo);
        query.setParameter("cardNo2", cardNo);
        query.setParameter("cardNo3", cardNo);
        query.setParameter("cardNo4", cardNo);
        query.setParameter("cardNo5", cardNo);
        query.setParameter("cardNo6", cardNo);
        query.setParameter("cardNo7", cardNo);
        query.setParameter("cardNo8", cardNo);
        query.setParameter("cardNo9", cardNo);
        query.setParameter("cardNo10", cardNo);
        query.setParameter("userId", excludeUserId);
        List<UserInfo> users = query.getResultList();
        if(users.size()>0){
            return users.get(0);
        }
        else {
            return null;
        }
    }

    public List<UserInfo> listByUserTypes(String userTypes){
        String jpql = "from UserInfo as user where user.userType in (" +userTypes+ ") order by user.userId ";
        Query query = em.createQuery(jpql);
       return (List<UserInfo>)query.getResultList();
    }
    public Integer save(UserInfo userInfo){
        if(userInfo.getUserId() == null){
            em.persist(userInfo);
        }
        else{
            em.merge(userInfo);
        }

        return userInfo.getUserId();
    }

    public List<UserInfo> listAll(){
        Query query = em.createQuery("from UserInfo as user order by user.userId ");
        return (List<UserInfo>)query.getResultList();
    }

    public void delete(Integer userId){
        String sql = "delete from deliverylog where pickupUser in (" + userId+") or deliveryMan in ("+userId+");" +
                " delete from openinglog where openingUser in (" + userId+"); " +
                " update boxinfo set deliveryMan = null where deliveryMan in ("+userId+");" +
                " update boxinfo set owner = null where owner in ("+userId+");" +
                " delete from userinfos where userId in (" + userId+"); ";
        Query query = em.createNativeQuery(sql);
        query.executeUpdate();
    }

    public void deleteByIds(String ids){
        String sql = "delete from deliverylog where pickupUser in (" + ids+") or deliveryMan in ("+ids+");" +
                " delete from openinglog where openingUser in (" + ids+"); " +
                " update boxinfo set deliveryMan = null where deliveryMan in ("+ids+");" +
                " update boxinfo set owner = null where owner in ("+ids+");" +
                " delete from userinfos where userId in (" + ids+"); ";
        Query query = em.createNativeQuery(sql);
        query.executeUpdate();
    }

    public List<UserInfo> listOwner(String filter){
        String jqpl = "from UserInfo as user where user.userType = 30 ";
        if(filter !=null && filter.length()>0){
            jqpl+= " and (user.userName like '%" + filter+"%' or user.phoneNo like '%"+ filter+"%') ";
        }
        jqpl += " order by user.userName ";
        Query query = em.createQuery(jqpl);
        return (List<UserInfo>) query.getResultList();
    }

    public List<UserInfo> listOwnerByRoom(String buildingNo, String unitNo,String floorNo,  String roomNo){
        String jqpl = "from UserInfo as user where user.userType = 30 ";
        if(buildingNo !=null && buildingNo.length()>0){
            jqpl+= " and (user.buildingNo like '%" + buildingNo+"%') ";
        }
        if(unitNo != null && unitNo.length()>0){
            jqpl+= " and (user.unitNo like '%" + unitNo+"%') ";
        }
        if(floorNo != null && floorNo.length()>0){
            jqpl += " and (user.floorNo like '%" + floorNo + "%') ";
        }
        if(roomNo != null && roomNo.length()>0){
            jqpl+= " and (user.roomNo like '%" + roomNo+"%') ";
        }
        jqpl += " order by user.buildingNo, user.unitNo, user.roomNo ";
        Query query = em.createQuery(jqpl);
        return (List<UserInfo>) query.getResultList();
    }


    public Integer changePassword(Integer userId, String oldPassword, String newPassword){
        UserInfo user = em.find(UserInfo.class, userId);
        if(user == null){
            return -1;
        }
        if(!user.getPassword().equals(oldPassword)){
            return -2;
        }

        user.setPassword(newPassword);
        em.merge(user);

        return 0;
    }
    // 得到没有取信件的用户信息
    public List<UserInfo> findUserInfoByNoPickupMail() {
        String jqpl = "from UserInfo as user where user in (select log.pickupUser from DeliveryLog as log where log.pickupTime is null )";
        Query query = em.createQuery(jqpl);
        return (List<UserInfo>) query.getResultList();
    }
}
