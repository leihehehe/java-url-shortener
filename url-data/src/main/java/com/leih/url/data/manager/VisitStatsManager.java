package com.leih.url.data.manager;

import com.leih.url.data.entity.VisitStats;

import java.util.List;

public interface VisitStatsManager {

    int countAll(Long accountNo, String code);

    List<VisitStats> paginateVisitRecord(String code, Long accountNo, int from, int size);
}
