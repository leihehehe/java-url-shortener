package com.leih.url.account.job;

import com.leih.url.account.annotation.CacheLock;
import com.leih.url.account.service.PlanService;
import com.leih.url.common.constant.RedisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PlanJobScheduler {
  @Autowired PlanService planService;

  @CacheLock(lockedPrefix = RedisKey.PLAN_DELETE_EXPIRED_SCHEDULER_KEY)
  @Scheduled(cron = "0 0 0 * * ?")//start at every 00:00
  public void deleteExpiredPaidPlans() {
    planService.deleteExpiredPlans();
  }
}
