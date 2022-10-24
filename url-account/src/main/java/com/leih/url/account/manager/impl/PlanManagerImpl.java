package com.leih.url.account.manager.impl;

import com.leih.url.account.dao.PlanRepository;
import com.leih.url.account.entity.Plan;
import com.leih.url.account.manager.PlanManager;
import com.leih.url.common.enums.PluginTypeEnum;
import com.leih.url.common.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class PlanManagerImpl implements PlanManager {
  @Autowired private PlanRepository planRepository;

  @Override
  public boolean addPlan(Plan plan) {
    try {
      planRepository.save(plan);
      return true;
    } catch (Exception e) {
      log.error("Failed to save plan: {}", e.getMessage());
      return false;
    }
  }

  @Override
  public Page<Plan> paginateAvailablePlan(int page, int size, Long accountNo) {
    PageRequest pageRequest = PageRequest.of(page, size);
    //        String today = TimeUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
    LocalDateTime now = LocalDateTime.now();
    Timestamp currentTime = Timestamp.valueOf(now);
    Page<Plan> availablePlans =
        planRepository.paginateAvailablePlans(accountNo, currentTime, pageRequest);

    return availablePlans;
  }

  @Override
  public Plan findByIdAndAccountNo(Long planId, Long accountNo) {
    return planRepository.findPlanByAccountNoAndId(accountNo, planId);
  }

  @Override
  public List<Plan> findAvailablePlans(Long accountNo) {
    List<Plan> availablePlans =
        planRepository.findAvailablePlans(
            accountNo, PluginTypeEnum.SHORT_LINK.name(), new Timestamp(new Date().getTime()));
    return availablePlans;
  }

  @Override
  public boolean addDayUsedTimes(Long planId, Long accountNo, Integer usedTimes) {
    try {
      int row = planRepository.addDayUsedTimes(planId, accountNo, usedTimes);
      return row == 1;
    } catch (Exception e) {
      log.error("Failed to add day used times: {}", e.getMessage());
      return false;
    }
  }

  @Override
  public boolean restoreUsedTimes(Long planId, Long accountNo, Integer usedTimes, String taskCreateDate) {
    try {
      planRepository.restoreDayUsedTimes(planId, accountNo, usedTimes,taskCreateDate);
      return true;
    } catch (Exception e) {
      log.error("Failed restore used times: {}", e.getMessage());
      return false;
    }
  }

  @Override
  public boolean batchUpdateUsedTimesToZero(Long accountNo, List<Long> planIds) {
    try {
      for (Long planId : planIds) {
        planRepository.updateDayUsedTimes(planId, accountNo, 0);
      }
      return true;
    } catch (Exception e) {
      log.error("Failed batch update for used times: {}", e.getMessage());
      return false;
    }
  }

  @Override
  public boolean deleteExpiredPlans() {
    try{
      planRepository.deleteExpiredPlans(new Timestamp(new Date().getTime()));
      return true;
    }catch (Exception e){
      log.error("Failed to delete expired plans: {}",e.getMessage());
      return false;
    }
  }
}
