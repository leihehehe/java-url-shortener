package com.leih.url.account.biz;

import com.leih.url.account.UrlAccountApplication;
import com.leih.url.account.dao.AccountRepository;
import com.leih.url.account.dao.PlanRepository;
import com.leih.url.account.entity.Account;
import com.leih.url.account.entity.Plan;
import com.leih.url.account.manager.PlanManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UrlAccountApplication.class)
@Slf4j
public class PlanTest {
    @Autowired
    PlanRepository planRepository;
    @Autowired
    AccountRepository accountRepository;
//    @Test
//    public void testPlan(){
//        Random random = new Random();
//        for(int i =0;i<10;i++){
//            Plan plan = new Plan();
//            plan.setAccountNo(random.nextInt(100));
//            planRepository.save(plan);
//        }
//
//    }

    @Test
    public void testDate(){
        LocalDateTime expiredDateTime = LocalDateTime.now().plusDays(1);
        log.info("{}",expiredDateTime);
        Date date = Date.from(expiredDateTime.atZone(ZoneId.systemDefault()).toInstant());
        log.info("{}",date);

    }
@Autowired
    private PlanManager planManager;
    @Test
    public void testFindAvailablePlans(){
        List<Plan> availablePlans = planManager.findAvailablePlans(790528839817101312L);
        availablePlans.forEach(obj->{
            log.info(obj.toString());
        });
    }
    @Test
    public void testAddDayUsedTimes(){
       planManager.addDayUsedTimes(790686270075080704L,790528839817101312L,1);
    }
//    @Test
//    public void testRestoreDayUsedTimes(){
//        planManager.restoreUsedTimes(790686270075080704L,790528839817101312L,1);
//    }

    @Test
    public void testBatchUpdateDayUsedTimes(){
        ArrayList<Long> arr = new ArrayList<>();
        arr.add(790686270075080704L);
        arr.add(790528840154386433L);
        planManager.batchUpdateUsedTimesToZero(790528839817101312L,arr);
    }
    @Test
    public void deleteExpiredDate(){
        planManager.deleteExpiredPlans();
    }

}
