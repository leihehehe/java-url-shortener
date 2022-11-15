package com.leih.url.data;

import com.leih.url.data.entity.VisitStats;
import com.leih.url.data.manager.VisitStatsManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DataApplication.class)
@Slf4j
public class VisitStatsTest {
  @Autowired private VisitStatsManager visitStatsManager;

  @Test
  public void testCount() {
    int count = visitStatsManager.countAll(790937829298405376L, "aVvyZg0");
    System.out.println(count);
  }
  @Test
  public void testPaginateVisitStats() {
    List<VisitStats> list = visitStatsManager.paginateVisitRecord("aVvyZg0", 790937829298405376L, 0, 10);
    System.out.println(Arrays.toString(list.toArray()));
  }
}
