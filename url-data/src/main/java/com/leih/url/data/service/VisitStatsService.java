package com.leih.url.data.service;

import com.leih.url.data.controller.request.VisitRecordPageRequest;

import java.util.Map;

public interface VisitStatsService {
    Map<String, Object> paginateVisitRecord(VisitRecordPageRequest request);
}
