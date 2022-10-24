package com.leih.url.link.feign;

import com.leih.url.common.util.JsonData;
import com.leih.url.link.controller.request.UsePlanRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "url-account-service")
public interface PlanFeignService {
    /**
     * Use plan
     * @param request
     * @return
     */
    @PostMapping(value = "/api/plan/v1/use",headers = {"rpc-token=${rpc.token}"})
    JsonData usePlan(@RequestBody UsePlanRequest request);
}
