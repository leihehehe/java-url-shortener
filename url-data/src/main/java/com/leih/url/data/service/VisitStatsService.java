package com.leih.url.data.service;

import com.leih.url.data.controller.request.*;
import com.leih.url.data.vo.VisitStatsVo;

import java.util.List;
import java.util.Map;

public interface VisitStatsService {
    Map<String, Object> paginateVisitRecord(VisitRecordPageRequest request);

    List<VisitStatsVo> queryRegion(RegionQueryRequest request);

    List<VisitStatsVo> queryVisitTrend(VisitTrendQueryRequest request);

    List<VisitStatsVo> queryFrequentSource(FrequentSourceRequest request);

    Map<String,List<VisitStatsVo>> queryDeviceInfo(DeviceQueryRequest request);
}
