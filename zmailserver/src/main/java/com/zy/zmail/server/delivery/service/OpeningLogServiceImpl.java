package com.zy.zmail.server.delivery.service;

import com.zy.zmail.server.cabinet.entity.CabinetNode;
import com.zy.zmail.server.common.util.DateUtil;
import com.zy.zmail.server.delivery.entity.DeliveryLog;
import com.zy.zmail.server.delivery.entity.LogBrief;
import com.zy.zmail.server.delivery.entity.OpeningBrief;
import com.zy.zmail.server.delivery.entity.OpeningLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by wenliz on 2017/3/2.
 */
@Service("openingLogService")
@Transactional
public class OpeningLogServiceImpl implements OpeningLogService {

    private static final Logger logger = LoggerFactory.getLogger(OpeningLogServiceImpl.class);

    @PersistenceContext
    private EntityManager em;

    public List<OpeningLog> listByCabinetId(Integer cabinetId){
        Query query = em.createQuery("from OpeningLog as log left join fetch log.boxInfo as box where box.cabinetId = :cabinetId");
        query.setParameter("cabinetId", cabinetId);
        return (List<OpeningLog>)query.getResultList();
    }
    public List<OpeningLog> listByCabinetId(Integer cabinetId, Integer periodType){
        if(periodType>5 || periodType <1) return null;

        Date startTime =  getStartTime(periodType);
        Query query = em.createQuery("from OpeningLog as log  left join fetch log.boxInfo as box " +
                "where log.openingTime>=:startTime and box.cabinetId = :cabinetId");
        query.setParameter("startTime", startTime);
        query.setParameter("cabinetId", cabinetId);
        return (List<OpeningLog>)query.getResultList();
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

    public Integer save(OpeningBrief brief){
        if(brief.getLogId() == null){
            em.persist(brief);
        }
        else{
            em.merge(brief);
        }
        return brief.getLogId();
    }

    public OpeningBrief getBriefByLogId(Integer logId){
        return  em.find(OpeningBrief.class, logId);
    }
}