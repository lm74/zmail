package com.zy.zmail.server.setting.service;

import com.zy.zmail.server.setting.entity.SystemOption;
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
 * Created by wenliz on 2017/1/23.
 */
@Service("optionService")
@Transactional
public class OptionServiceImpl  implements OptionService{
    private static final Logger logger = LoggerFactory.getLogger(OptionServiceImpl.class);

    @PersistenceContext
    private EntityManager em;

    public SystemOption getById(Integer optionId){
        return em.find(SystemOption.class, optionId);
    }

    public List<SystemOption> listAll(){
        Query query = em.createQuery("from SystemOption as option");
        return (List<SystemOption>)query.getResultList();
    }

    public Integer save(SystemOption option){
        em.merge(option);
        return option.getOptionId();
    }
}
