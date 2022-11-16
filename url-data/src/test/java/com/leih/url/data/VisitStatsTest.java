package com.leih.url.data;

import com.leih.url.data.config.ClickHouseTemplate;
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
import java.util.Map;

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

  @Test
  public void testRegionQuery(){
    String sql = "select country,sum(pv) pv_count,sum(uv) uv_count,count(DISTINCT ip) ip_count from visit_stats WHERE account_no = '%s' and code ='%s' and toYYYYMMDD(start_time) BETWEEN '%s' and '%s' group by country order by pv_count desc";
    String regionSql = String.format(sql, 790937829298405376L, "aVvyZg0", "20200213", "20221228");
    List<Map<String, Object>> maps = ClickHouseTemplate.sqlQuery(regionSql);
    System.out.println(maps);
  }
}
