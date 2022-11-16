package com.leih.url.data.manager;

import com.leih.url.data.config.ClickHouseTemplate;
import com.leih.url.data.entity.VisitStats;
import com.leih.url.data.repository.VisitStatsRepository;
import com.leih.url.data.vo.VisitStatsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
    List<Map<String, Object>> maps = ClickHouseTemplate.sqlQuery(regionSql);
    List<VisitStats> visitStatsList =
        maps.stream().map(this::regionConvertToVisitStats).collect(Collectors.toList());
    return visitStatsList;
  }

  @Override
  public List<VisitStats> queryDayVisitTrend(Long accountNo, String code, String startTime, String endTime) {
    String sql =
            "select toYYYYMMDD(start_time) date_time_str,sum(if(is_new='1', visit_stats.uv,0)) new_uv_count,sum(visit_stats.uv) uv_count,sum(pv) pv_count,count(DISTINCT ip) ip_count from visit_stats WHERE account_no = '%s' and code ='%s' and toYYYYMMDD(start_time) BETWEEN '%s' and '%s' group by date_time_str order by date_time_str desc";
    String trendSql = String.format(sql, accountNo, code, startTime, endTime);
    List<Map<String, Object>> maps = ClickHouseTemplate.sqlQuery(trendSql);
    List<VisitStats> visitStatsList = maps.stream().map(this::trendConvertToVisitStats).collect(Collectors.toList());
    return visitStatsList;
  }

  private VisitStats regionConvertToVisitStats(Map<String, Object> map) {
    VisitStats visitStats =
        VisitStats.builder()
            .pvCount(Long.parseLong((String) map.getOrDefault("pv_count",null)))
            .country((String) map.getOrDefault("country",null))
            .uvCount(Long.parseLong((String) map.getOrDefault("uv_count",null)))
            .ipCount(Long.parseLong((String) map.getOrDefault("ip_count",null)))
            .build();
    return visitStats;
  }
  private VisitStats trendConvertToVisitStats(Map<String, Object> map) {
    VisitStats visitStats =
            VisitStats.builder()
                    .dateTimeStr((String) map.getOrDefault("date_time_str",null))
                    .country((String) map.getOrDefault("new_uv_count",null))
                    .uvCount(Long.parseLong((String) map.getOrDefault("uv_count",null)))
                    .ipCount(Long.parseLong((String) map.getOrDefault("ip_count",null)))
                    .pvCount(Long.parseLong((String) map.getOrDefault("pv_count",null)))
                    .build();
    return visitStats;
  }
}
