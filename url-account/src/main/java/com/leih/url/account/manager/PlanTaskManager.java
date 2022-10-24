package com.leih.url.account.manager;

import com.leih.url.account.entity.PlanTask;

public interface PlanTaskManager {
    boolean addPlanTask(PlanTask planTask);
    PlanTask findByIdAndAccountNo(Long id, Long accountNo);
    boolean deleteByIdAndAccountNo(Long id, Long accountNo);
}
