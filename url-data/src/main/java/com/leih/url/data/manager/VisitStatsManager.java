package com.leih.url.data.manager;

import com.leih.url.data.entity.VisitStats;
import com.leih.url.data.vo.VisitStatsVo;

import java.util.List;

public interface VisitStatsManager {

    int countAll(Long accountNo, String code);

    List<VisitStats> paginateVisitRecord(String code, Long accountNo, int from, int size);

    List<VisitStats> queryRegion(String code, String startTime, String endTime, Long accountNo);

    List<VisitStats> queryDayVisitTrend(Long accountNo, String code, String startTime, String endTime);

}
