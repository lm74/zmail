package com.zy.zmail.server.cabinet.service;

import com.zy.zmail.server.cabinet.entity.BoxInfo;
import com.zy.zmail.server.cabinet.entity.CabinetNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by wenliz on 2017/2/10.
 */
@Service("boxService")
@Transactional
public class BoxServiceImpl implements BoxService{
    private static final Logger logger = LoggerFactory.getLogger(BoxServiceImpl.class);

    @PersistenceContext
    private EntityManager em;

    public Integer save(BoxInfo boxInfo){
        if(boxInfo.getBoxId() == null){
            em.persist(boxInfo);
        }
        else{
            em.merge(boxInfo);
        }
        return boxInfo.getBoxId();
    }
    public BoxInfo getById(Integer boxId){
        return em.find(BoxInfo.class, boxId);
    }

    public void deleteByCabinetId(Integer cabinetId){
        String sql = "Delete from boxInfo where cabinetId = :cabinetId";
        Query query = em.createNativeQuery(sql);
        query.setParameter("cabinetId", cabinetId);
        query.executeUpdate();
    }

    public List<BoxInfo> listByCabinetId(Integer cabinetId){
        String jqpl = "from BoxInfo b where b.cabinetId=:cabinetId order by b.sequence";
        Query query = em.createQuery(jqpl);
        query.setParameter("cabinetId", cabinetId);
        return (List<BoxInfo>)query.getResultList();
    }



    public List<BoxInfo> listAvailabe(Integer cabinetId){
        String jqpl = "from BoxInfo as b where b.cabinetId=:cabinetId  and  b.locked = false and b.used=false " +
                " order by b.sequence";
        Query query = em.createQuery(jqpl);
        query.setParameter("cabinetId", cabinetId);
        return (List<BoxInfo>)query.getResultList();
    }

    public List<CabinetNode> cabinetAvailableCount(String boxTypes){
        String sql = "select b.cabinetId,c.cabinetNo, count(b.boxId) as count " +
                " from BoxInfo as  b left join CabinetInfo c on c.cabinetId = b.cabinetId" +
                " where b.locked=false and b.used = false and b.boxType in (" + boxTypes+")"+
                " group by b.cabinetId, c.cabinetNo ";
        Query query = em.createNativeQuery(sql);
        List results = query.getResultList();
        List<CabinetNode> nodes = new ArrayList<>();
        if(results == null || results.size() == 0) return results;

        for(int i=0; i<results.size(); i++){
            Object[] objects = (Object[])results.get(i);
            CabinetNode node = new CabinetNode();
            try {
                node.setCabinetId(Integer.valueOf(objects[0].toString()));
                node.setCabinetNo(Integer.valueOf(objects[1].toString()));
                node.setCount(Integer.valueOf(objects[2].toString()));
                nodes.add(node);
            }
            catch (Exception e){

            }
        }

        return nodes;
    }

    public List<BoxInfo> listApplyMail(Integer cabinetId){
        String jqpl = "from BoxInfo b where b.cabinetId=:cabinetId and b.boxType in (0, 1) order by b.sequence";
        Query query = em.createQuery(jqpl);
        query.setParameter("cabinetId", cabinetId);
        return (List<BoxInfo>)query.getResultList();
    }
}
