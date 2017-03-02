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

    public UserInfo getByUserName(String userName){
        Query query = em.createQuery("from UserInfo user where user.userName = :userName");
        query.setParameter("userName", userName);
        return (UserInfo)query.getSingleResult();
    }
    public UserInfo getByPhoneNo(String phoneNo){
        Query query = em.createQuery("from UserInfo user where user.phoneNo = :phoneNo");
        query.setParameter("phoneNo", phoneNo);
        return (UserInfo)query.getSingleResult();

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

    public List<UserInfo> listByUserTypes(String userTypes){
        String jpql = "from UserInfo as user where user.userType in (" +userTypes+ ")";
        Query query = em.createQuery(jpql);
       return (List<UserInfo>)query.getResultList();
    }
    public Integer save(UserInfo userInfo){
        if(userInfo.getUserId() == null){
            userInfo.setPassword(KeySecurity.encrypt(userInfo.getPassword()));
            em.persist(userInfo);
        }
        else{
            em.merge(userInfo);
        }

        return userInfo.getUserId();
    }

    public List<UserInfo> listAll(){
        Query query = em.createQuery("from UserInfo as user");
        return (List<UserInfo>)query.getResultList();
    }

    public void delete(Integer userId){
        UserInfo user = em.find(UserInfo.class, userId);
        if(user != null){
            em.remove(user);
        }
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

    public List<UserInfo> listOwnerByRoom(String buildingNo, String unitNo, String roomNo){
        String jqpl = "from UserInfo as user where user.userType = 30 ";
        if(buildingNo !=null && buildingNo.length()>0){
            jqpl+= " and (user.buildingNo like '%" + buildingNo+"%') ";
        }
        if(unitNo != null && unitNo.length()>0){
            jqpl+= " and (user.unitNo like '%" + unitNo+"%') ";
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
        String encry =  KeySecurity.encrypt(oldPassword);
        if(!user.getPassword().equals(oldPassword)){
            return -2;
        }
        String newEncry = KeySecurity.encrypt(newPassword);
        user.setPassword(newEncry);
        em.merge(user);

        return 0;
    }
}
