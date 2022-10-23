package com.leih.url.account.controller;

import com.leih.url.account.controller.request.PlanPageRequest;
import com.leih.url.account.controller.request.UsePlanRequest;
import com.leih.url.account.service.PlanService;
import com.leih.url.account.vo.PlanVo;
import com.leih.url.common.util.JsonData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/plan/v1")
public class PlanController {
    @Autowired
    private PlanService planService;
    @Value("${rpc.token}")
    private String rpcToken;
    @PostMapping("page")
    public JsonData paginateAvailablePlans(@RequestBody PlanPageRequest request){
        Map<String,Object> map = planService.paginateAvailablePlans(request);
        return JsonData.buildSuccess(map);
    }
    @GetMapping("detail/{planId}")
    public JsonData detail(@PathVariable("planId") Long planId){
        PlanVo plan = planService.getPlanDetail(planId);
        return JsonData.buildSuccess(plan);
    }
    @PostMapping("use")
    public JsonData usePlan(@RequestBody UsePlanRequest usePlanRequest, HttpServletRequest request){
        String rpcRequestToken = request.getHeader("rpc-token");
        if(rpcToken.equalsIgnoreCase(rpcRequestToken)){
            JsonData jsonData =planService.usePlan(usePlanRequest);
            return jsonData;
        }else {
            return JsonData.buildError("Forbidden");
        }
    }
}
