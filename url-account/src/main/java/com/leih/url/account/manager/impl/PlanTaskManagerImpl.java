package com.leih.url.account.manager.impl;

import com.leih.url.account.dao.PlanTaskRepository;
import com.leih.url.account.entity.PlanTask;
import com.leih.url.account.manager.PlanTaskManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PlanTaskManagerImpl implements PlanTaskManager {
    @Autowired
    PlanTaskRepository planTaskRepository;
    @Override
    public boolean addPlanTask(PlanTask planTask) {
        try{
            planTaskRepository.save(planTask);
            return true;
        }catch (Exception e){
            log.error("Failed to save plan task :{}",e.getMessage());
            return false;
        }
    }

    @Override
    public PlanTask findByIdAndAccountNo(Long id, Long accountNo) {
        return planTaskRepository.findByIdAndAccountNo(id,accountNo);
    }

    @Override
    public boolean deleteByIdAndAccountNo(Long id, Long accountNo) {
        try{
            planTaskRepository.deleteByIdAndAccountNo(id,accountNo);
            return true;
        }catch (Exception e){
            log.error("Failed to delete plan task: {}",e.getMessage());
            return false;
        }
    }
}
