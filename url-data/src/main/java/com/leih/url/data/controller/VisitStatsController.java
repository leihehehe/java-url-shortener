package com.leih.url.data.controller;

import com.leih.url.common.enums.BizCodeEnum;
import com.leih.url.common.util.JsonData;
import com.leih.url.data.controller.request.VisitRecordPageRequest;
import com.leih.url.data.service.VisitStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/visit_stats/v1")
public class VisitStatsController {
    @Autowired
    private VisitStatsService visitStatsService;
    @PostMapping("/page_record")
    private JsonData paginateVisitRecord(@RequestBody VisitRecordPageRequest request){
        int total = request.getSize() * request.getPage();
        if(total>1000){
            return JsonData.buildResult(BizCodeEnum.DATA_QUERY_EXCEEDS_LIMIT);
        }
        Map<String,Object> pageResult = visitStatsService.paginateVisitRecord(request);
        return JsonData.buildSuccess(pageResult);
    }
}
