package com.leih.url.data.manager;

import com.leih.url.data.config.ClickHouseConfig;
import com.leih.url.data.entity.VisitStats;
import com.leih.url.data.repository.VisitStatsRepository;
import com.leih.url.data.vo.VisitStatsVo;
import com.mysql.cj.log.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class VisitStatsManagerImpl implements VisitStatsManager {
  @Autowired VisitStatsRepository visitStatsRepository;

  @Override
  public int countAll(Long accountNo, String code) {
    int result = visitStatsRepository.countAllByAccountNoAndCode(accountNo, code);
    return result;
  }

  @Override
  public List<VisitStats> paginateVisitRecord(String code, Long accountNo, int from, int size) {
    List<VisitStats> visitStatsList =
        visitStatsRepository.paginateVisitRecord(accountNo, code, from, size);
    return visitStatsList;
  }

  @Override
  public List<VisitStats> queryRegion(
      String code, String startTime, String endTime, Long accountNo) {
    String sql =
        "select country,sum(pv) pv_count,sum(uv) uv_count,count(DISTINCT ip) ip_count from visit_stats WHERE account_no = '%s' and code ='%s' and toYYYYMMDD(start_time) BETWEEN '%s' and '%s' group by country order by pv_count desc";
    String regionSql = String.format(sql, accountNo, code, startTime, endTime);
    List<Map<String, Object>> maps = ClickHouseConfig.sqlQuery(regionSql);
    List<VisitStats> visitStatsList =
        maps.stream().map(this::convertToVisitStats).collect(Collectors.toList());
    return visitStatsList;
  }

  private VisitStats convertToVisitStats(Map<String, Object> map) {
    VisitStats visitStats =
        VisitStats.builder()
            .pvCount(Long.parseLong((String) map.get("pv_count")))
            .country((String) map.get("country"))
            .uvCount(Long.parseLong((String) map.get("uv_count")))
            .ipCount(Long.parseLong((String) map.get("ip_count")))
            .build();
    return visitStats;
  }
}
