package com.leih.url.data.manager;

import com.leih.url.common.enums.DateTimeFieldEnum;
import com.leih.url.common.enums.DeviceEnum;
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
    String sql = "select * from visit_stats where account_no= '%s' and code= '%s' order by timestamp desc limit %s,%s";
    String visitSql = String.format(sql, accountNo, code, from, size);
    List<Map<String, Object>> maps = ClickHouseTemplate.sqlQuery(visitSql);
    List<VisitStats> visitStatsList =
            maps.stream().map(this::visitConvertToVisitStats).collect(Collectors.toList());
    return visitStatsList;
  }

  private VisitStats visitConvertToVisitStats(Map<String, Object> map) {
    VisitStats visitStats =
            VisitStats.builder()
                    .deviceType((String) map.getOrDefault("device_type",null))
                    .ip((String) map.getOrDefault("ip",null))
                    .referer((String)map.getOrDefault("referer",null))
                    .endTime((String) map.getOrDefault("start_time",null))
                    .startTime((String) map.getOrDefault("end_time",null))
                    .os((String) map.getOrDefault("os",null))
                    .accountNo(Long.valueOf((String)map.getOrDefault("account_no",null)))
                    .code((String) map.getOrDefault("code",null))
                    .isNew(Integer.valueOf((String) map.getOrDefault("is_new",null)))
                    .country((String) map.getOrDefault("country",null))
                    .browserName((String) map.getOrDefault("browser_name",null))
                    .pv(Long.valueOf((String)map.getOrDefault("pv",null)))
                    .uv(Long.valueOf((String)map.getOrDefault("uv",null)))
                    .visitTime(Long.valueOf((String) map.getOrDefault("timestamp",null)))
                    .build();
    return visitStats;
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
  public List<VisitStats> queryVisitTrend(Long accountNo, String code, String startTime, String endTime, DateTimeFieldEnum type) {
    String sql=null;
    switch (type){
      case DAY -> sql="select toYYYYMMDD(start_time) date_time_str,sum(if(is_new='1', visit_stats.uv,0)) new_uv_count,sum(visit_stats.uv) uv_count,sum(pv) pv_count,count(DISTINCT ip) ip_count from visit_stats WHERE account_no = '%s' and code ='%s' and toYYYYMMDD(start_time) BETWEEN '%s' and '%s' group by date_time_str order by date_time_str desc";
      case HOUR -> sql="select toHour(start_time) date_time_str,sum(if(is_new='1', visit_stats.uv,0)) new_uv_count,sum(visit_stats.uv) uv_count,sum(pv) pv_count,count(DISTINCT ip) ip_count from visit_stats WHERE account_no = '%s' and code ='%s' and toYYYYMMDDhhmmss(start_time) BETWEEN '%s' and '%s' group by date_time_str order by date_time_str desc";
      case MINUTE -> sql="select toMinute(start_time) date_time_str,sum(if(is_new='1', visit_stats.uv,0)) new_uv_count,sum(visit_stats.uv) uv_count,sum(pv) pv_count,count(DISTINCT ip) ip_count from visit_stats WHERE account_no = '%s' and code ='%s' and toYYYYMMDDhhmmss(start_time) BETWEEN '%s' and '%s' group by date_time_str order by date_time_str desc";
      case WEEK -> sql="select toWeek(start_time) date_time_str,sum(if(is_new='1', visit_stats.uv,0)) new_uv_count,sum(visit_stats.uv) uv_count,sum(pv) pv_count,count(DISTINCT ip) ip_count from visit_stats WHERE account_no = '%s' and code ='%s' and toYYYYMMDD(start_time) BETWEEN '%s' and '%s' group by date_time_str order by date_time_str desc";
    }
    String trendSql = String.format(sql, accountNo, code, startTime, endTime);
    List<Map<String, Object>> maps = ClickHouseTemplate.sqlQuery(trendSql);
    List<VisitStats> visitStatsList = maps.stream().map(this::trendConvertToVisitStats).collect(Collectors.toList());
    return visitStatsList;
  }

  @Override
  public List<VisitStats> queryFrequentSource(Long accountNo, String code, String startTime, String endTime, int size) {
    String sql = "select referer,sum(pv) pv_count from visit_stats WHERE  account_no = %s and code ='%s' and toYYYYMMDD(start_time) BETWEEN '%s' and '%s' group by referer order by pv_count desc";
    String sourceSql = String.format(sql, accountNo, code, startTime, endTime);
    List<Map<String, Object>> maps = ClickHouseTemplate.sqlQuery(sourceSql);
    List<VisitStats> visitStatsList = maps.stream().map(this::sourceConvertToVisitStats).collect(Collectors.toList());
    return visitStatsList;
  }

  @Override
  public List<VisitStats> queryDeviceInfo(Long accountNo, String code, String startTime, String endTime, DeviceEnum type) {
    String sql=null;
    switch (type){
      case OS -> sql="select os ,sum(pv) pv_count from visit_stats WHERE account_no = '%s' and code ='%s' and toYYYYMMDD(start_time) BETWEEN '%s' and '%s' group by os order by pv_count desc";
      case DEVICE_TYPE -> sql="select device_type ,sum(pv) pv_count from visit_stats WHERE account_no = '%s' and code ='%s' and toYYYYMMDD(start_time) BETWEEN '%s' and '%s' group by device_type order by pv_count desc";
      case BROWSER_NAME -> sql="select browser_name ,sum(pv) pv_count from visit_stats WHERE account_no = '%s' and code ='%s' and toYYYYMMDD(start_time) BETWEEN '%s' and '%s' group by browser_name order by pv_count desc";
    }
    String deviceSql = String.format(sql, accountNo, code, startTime, endTime);
    List<Map<String, Object>> maps = ClickHouseTemplate.sqlQuery(deviceSql);
    List<VisitStats> visitStatsList = maps.stream().map(this::deviceConvertToVisitStats).collect(Collectors.toList());
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
                    .newUVCount(Long.valueOf((String) map.getOrDefault("new_uv_count",null)))
                    .uvCount(Long.parseLong((String) map.getOrDefault("uv_count",null)))
                    .ipCount(Long.parseLong((String) map.getOrDefault("ip_count",null)))
                    .pvCount(Long.parseLong((String) map.getOrDefault("pv_count",null)))
                    .build();
    return visitStats;
  }

  public VisitStats sourceConvertToVisitStats(Map<String, Object> map){
    VisitStats visitStats = VisitStats.builder().referer((String) map.getOrDefault("referer", null))
            .pvCount(Long.parseLong((String) map.getOrDefault("pv_count", null)))
            .build();
    return visitStats;
  }
  public VisitStats deviceConvertToVisitStats(Map<String, Object> map){
    VisitStats visitStats = VisitStats.builder().os((String) map.getOrDefault("os", null))
            .pvCount(Long.parseLong((String) map.getOrDefault("pv_count", null)))
            .browserName((String) map.getOrDefault("browser_name", null))
            .deviceType((String) map.getOrDefault("device_type", null))
            .build();
    return visitStats;
  }
}
