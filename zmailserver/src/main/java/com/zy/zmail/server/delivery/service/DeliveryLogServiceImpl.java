package com.zy.zmail.server.delivery.service;

import com.zy.zmail.server.cabinet.entity.CabinetNode;
import com.zy.zmail.server.cabinet.service.CabinetServiceImpl;
import com.zy.zmail.server.common.util.DateUtil;
import com.zy.zmail.server.delivery.entity.DeliveryLog;
import com.zy.zmail.server.delivery.entity.LogBrief;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by wenliz on 2017/2/14.
 */
@Service("deliveryLogService")
@Transactional
public class DeliveryLogServiceImpl implements DeliveryLogService {

    private static final Logger logger = LoggerFactory.getLogger(DeliveryLogServiceImpl.class);

    @PersistenceContext
    private EntityManager em;

    public List<DeliveryLog> listByCabinetId(Integer cabinetId, Integer pickedup){
        String jqpl = " from DeliveryLog as log left join fetch log.boxInfo as box where box.cabinetId = :cabinetId ";

        Calendar endTime = Calendar.getInstance();
        if(pickedup > 0){
            if(pickedup == 2){
                jqpl += " and  (log.pickupTime is null) ";
            }else {
                endTime.add(Calendar.HOUR_OF_DAY, -pickedup);
                jqpl += " and  (log.pickupTime is null) and (log.deliveryTime<:endTime) ";
            }
        }
        Query query = em.createQuery(jqpl);
        query.setParameter("cabinetId", cabinetId);
        if(pickedup>0){
            if(pickedup != 2){
                query.setParameter("endTime",  endTime.getTime());
            }
        }
        return (List<DeliveryLog>)query.getResultList();
    }
    public List<DeliveryLog> listByCabinetId(Integer cabinetId, Integer periodType, Integer pickedup){
        if(periodType>5 || periodType <1) {
            return null;
        }

        Date startTime =  getStartTime(periodType);
        String jqpl = " from DeliveryLog as log  left join fetch log.boxInfo as box " +
            "where log.deliveryTime>=:startTime and box.cabinetId = :cabinetId ";
        Calendar endTime = Calendar.getInstance();
        if(pickedup > 0){
            if(pickedup == 2){
                jqpl += " and  (log.pickupTime is null) ";
            }else {
                endTime.add(Calendar.HOUR_OF_DAY, -pickedup);
                jqpl += " and  (log.pickupTime is null) and (log.deliveryTime<:endTime) ";
            }
        }
        Query query = em.createQuery(jqpl);
        query.setParameter("startTime", startTime);
        query.setParameter("cabinetId", cabinetId);
        if(pickedup>0){
            if(pickedup != 2){
                query.setParameter("endTime",  endTime.getTime());
            }
        }
        return (List<DeliveryLog>)query.getResultList();
    }

    public List<DeliveryLog> listByOwner(Integer cabinetId, Integer ownerId, boolean pickuped){
        String jqpl = "from DeliveryLog as log left join fetch log.boxInfo as box " +
                " left join fetch log.pickupUser as user "+
                " where box.cabinetId = :cabinetId and user.userId=:ownerId";
        if(pickuped){
            jqpl += " and  not (log.pickupTime is null)";
        }
        else {
            jqpl += " and log.pickupTime is null";
        }
        jqpl += " order by log.deliveryTime desc ";
        Query query = em.createQuery(jqpl);
        query.setParameter("cabinetId", cabinetId);
        query.setParameter("ownerId", ownerId);
        return query.getResultList();
    }

    public List<DeliveryLog> listByOwner(Integer ownerId){
        String jqpl = "from DeliveryLog as log left join fetch log.boxInfo as box " +
                " left join fetch log.pickupUser as user "+
                " where  user.userId=:ownerId";

            jqpl += " and log.pickupTime is null";

        Query query = em.createQuery(jqpl);
        query.setParameter("ownerId", ownerId);
        return query.getResultList();
    }

    public List<LogBrief> listByBoxId(Integer boxId){
        String jqpl = "from LogBrief as log "+
                " where  log.boxId=:boxId";

        jqpl += " and log.pickupTime is null";

        Query query = em.createQuery(jqpl);
        query.setParameter("boxId", boxId);
        return query.getResultList();
    }

    public List<CabinetNode> listAllByOwner(Integer ownerId, Integer pickuped){
        String sql = " select c.cabinetId, c.cabinetNo, count(log.logId) as countNumber " +
                "from DeliveryLog as log left join  boxInfo as box on log.boxId = box.boxId " +
                " left join  cabinetInfo c on box.cabinetId = c.cabinetId   "+
                " where  log.pickupUser=:ownerId ";
        if(pickuped==1){
            sql += " and  not (log.pickupTime is null)";
        }
        else {
            sql += " and log.pickupTime is null";
        }
        sql += " group by c.cabinetId, c.cabinetNo  ";

        Query query = em.createNativeQuery(sql);
        query.setParameter("ownerId", ownerId);
        List results = query.getResultList();
        List<CabinetNode> nodes = new ArrayList<>();
        for(int i=0; i<results.size(); i++){
            Object[] objects = (Object[])results.get(i);
            CabinetNode node = new CabinetNode();
            node.setCabinetId(Integer.valueOf(objects[0].toString()));
            node.setCabinetNo(Integer.valueOf(objects[1].toString()));
            node.setCount(Integer.valueOf(objects[2].toString()));
            if(node.getCount()==0) continue;
            nodes.add(node);
        }

        return nodes;
    }

    public List<DeliveryLog> listByDelivery(Integer cabinetId, Integer deliveryMan, Integer pickuped, Integer periodType){
        Date startTime = getStartTime(periodType);

        String jqpl = "from DeliveryLog as log left join fetch log.boxInfo as box " +
                " left join fetch log.deliveryMan as user "+
                " where  box.cabinetId = :cabinetId and user.userId=:deliveryMan ";
        if(periodType > 0){
            jqpl += " and log.deliveryTime>=:startTime ";
        }
        if(pickuped==1){
            jqpl += " and  not (log.pickupTime is null)";
        }
        else {
            jqpl += " and log.pickupTime is null";
        }
        Query query = em.createQuery(jqpl);
        query.setParameter("cabinetId", cabinetId);
        query.setParameter("deliveryMan", deliveryMan);
        if(periodType > 0) {
            query.setParameter("startTime", startTime);
        }
        return query.getResultList();
    }

    private Date getStartTime(Integer periodType) {
        Date startTime = null;
        switch (periodType){
            case 1:
                startTime = DateUtil.getDayBegin();
                break;
            case 2:
                startTime= DateUtil.getBeginDayOfWeek();
                break;
            case 3:
                startTime = DateUtil.getStartOfMonth();
                break;
            case 4:
                startTime = DateUtil.getBeforeOfNow(1);
                break;
            case 5:
                startTime = DateUtil.getBeforeOfNow(3);
                break;
        }
        return startTime;
    }

    public Integer save(LogBrief brief){
        if(brief.getLogId() == null){
            em.persist(brief);
        }
        else{
            em.merge(brief);
        }
        return brief.getLogId();
    }

    public LogBrief getBriefByLogId(Integer logId){
       return  em.find(LogBrief.class, logId);
    }

    public Integer deleteByCabinetId(Integer cabinetId){
        String jqpl = "delete from DeliveryLog  where boxId in (select boxId from boxInfo where cabinetId=:cabinetId)";
        Query query = em.createNativeQuery(jqpl);
        query.setParameter("cabinetId", cabinetId);
        return query.executeUpdate();
    }
}
