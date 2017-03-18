package com.zy.zmail.server.cabinet.service;

import com.zy.zmail.server.cabinet.entity.CabinetInfo;
import com.zy.zmail.server.user.service.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by wenliz on 2017/2/8.
 */
@Service("cabinetService")
@Transactional
public class CabinetServiceImpl implements CabinetService{
    private static final Logger logger = LoggerFactory.getLogger(CabinetServiceImpl.class);

    @PersistenceContext
    private EntityManager em;
    public List<CabinetInfo> listAll(){
        String jpql = "from CabinetInfo";
        Query query = em.createQuery(jpql);
        return query.getResultList();
    }

    public Integer save(CabinetInfo cabinetInfo){
        if(cabinetInfo.getCabinetId() == null){
            em.persist(cabinetInfo);
        }
        else{
            em.merge(cabinetInfo);
        }
        return cabinetInfo.getCabinetId();
    }

    public void delete(Integer cabinetId){
        CabinetInfo cabinetInfo = em.find(CabinetInfo.class, cabinetId);
        em.remove(cabinetInfo);
    }

    public CabinetInfo getByCabinetNo(String cabinetNo){
        String jpql = "from CabinetInfo c where c.cabinetNo= :cabinetNo";
        Query query = em.createQuery(jpql);
        query.setParameter("cabinetNo", cabinetNo);
        try {
            CabinetInfo cabinetInfo = (CabinetInfo)query.getResultList().get(0);
            return cabinetInfo;
        }
        catch (Exception e){
            return null;
        }
    }

    public Integer sameCabinetNo(String cabinetNo){
        String jpql = "from CabinetInfo c where c.cabinetNo= :cabinetNo";
        Query query = em.createQuery(jpql);
        query.setParameter("cabinetNo", cabinetNo);
        List cabinets = query.getResultList();
        return cabinets.size();
    }

    public CabinetInfo getByCabinetId(Integer cabinetId){
        String jpql = "from CabinetInfo c where c.cabinetId= :cabinetId";
        Query query = em.createQuery(jpql);
        query.setParameter("cabinetId", cabinetId);
        List cabinets = query.getResultList();
        if(cabinets.size() == 0){
            return null;
        }
        return (CabinetInfo)cabinets.get(0);
    }

}
