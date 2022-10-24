package com.leih.url.account.manager;

import com.leih.url.account.entity.Plan;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface PlanManager {
    boolean addPlan(Plan plan);
    Page<Plan> paginateAvailablePlan(int page, int size, Long accountNo);
    Plan findByIdAndAccountNo(Long planId, Long accountNo);
    List<Plan> findAvailablePlans(Long accountNo);
    boolean addDayUsedTimes(Long planId, Long accountNo, Integer usedTimes);
    boolean restoreUsedTimes(Long planId, Long accountNo, Integer usedTimes,String taskCreateDate);
    boolean batchUpdateUsedTimesToZero(Long accountNo, List<Long> planIds);
    boolean deleteExpiredPlans();
}
