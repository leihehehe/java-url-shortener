package com.leih.url.account.biz;

import com.leih.url.account.UrlAccountApplication;
import com.leih.url.account.entity.PlanTask;
import com.leih.url.account.manager.PlanTaskManager;
import com.leih.url.common.enums.TaskStateEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UrlAccountApplication.class)
@Slf4j
public class PlanTaskTest {
    @Autowired
    private PlanTaskManager planTaskManager;
    @Test
    public void addTask(){
        PlanTask planTask = new PlanTask();
        planTask.setPlanId(123L);
        planTask.setAccountNo(12333L);
        planTask.setLockState(TaskStateEnum.LOCK.name());
        planTask.setUseTimes(2);
        planTask.setBizId("wad123123sa");
        planTaskManager.addPlanTask(planTask);
    }
    @Test
    public void deleteTask(){
        planTaskManager.deleteByIdAndAccountNo(2L,12333L);
    }
    @Test
    public void findTask(){
        PlanTask planTask = planTaskManager.findByIdAndAccountNo(3L, 12333L);
        log.info("{}",planTask);
    }
}
