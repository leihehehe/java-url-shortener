package com.leih.url.account.service;

import com.leih.url.account.controller.request.PlanPageRequest;
import com.leih.url.account.vo.PlanVo;
import com.leih.url.common.model.EventMessage;

import java.util.Map;

public interface PlanService {
    void handlePlanMessage(EventMessage eventMessage);

    Map<String, Object> paginateAvailablePlans(PlanPageRequest request);

    PlanVo getPlanDetail(Long planId);
}
