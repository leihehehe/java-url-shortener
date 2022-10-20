package com.leih.url.account.manager.impl;

import com.leih.url.account.dao.PlanRepository;
import com.leih.url.account.entity.Plan;
import com.leih.url.account.manager.PlanManager;
import com.leih.url.common.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
@Component
@Slf4j
public class PlanManagerImpl implements PlanManager {
    private PlanRepository planRepository;
    @Override
    public boolean addPlan(Plan plan) {
        try{
            planRepository.save(plan);
            return true;
        }catch (Exception e){
            log.error("Failed to save plan: {}",e.getMessage());
            return false;
        }

    }

    @Override
    public Page<Plan> paginateAvailablePlan(int page, int size, Long accountNo) {
        PageRequest pageRequest = PageRequest.of(page, size);
        String today = TimeUtil.format(new Date(), "yyyy-MM-dd");
        Page<Plan> availablePlans = planRepository.findAvailablePlans(accountNo, today,pageRequest);

        return availablePlans;
    }

    @Override
    public Plan findByIdAndAccountNo(Long planId, Long accountNo) {
        return planRepository.findPlanByAccountNoAndId(accountNo, planId);
    }

    @Override
    public boolean addDayUsedTimes(Long planId, Long accountNo, int dayUsedTimes) {
        try{
            planRepository.updateDayUsedTimes(planId,accountNo,dayUsedTimes);
            return true;
        }catch (Exception e){
            log.error("Failed to update plan: {}",e.getMessage());
            return false;
        }
    }
}
