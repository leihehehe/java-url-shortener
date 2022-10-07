package com.leih.url.account.biz;

import com.leih.url.account.UrlAccountApplication;
import com.leih.url.account.dao.AccountRepository;
import com.leih.url.account.dao.PlanRepository;
import com.leih.url.account.entity.Account;
import com.leih.url.account.entity.Plan;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UrlAccountApplication.class)
@Slf4j
public class PlanTest {
    @Autowired
    PlanRepository planRepository;
    @Autowired
    AccountRepository accountRepository;
    @Test
    public void testPlan(){
        Random random = new Random();
        for(int i =0;i<10;i++){
            Plan plan = new Plan();
            plan.setAccountNo(random.nextInt(100));
            planRepository.save(plan);
        }

    }
}
