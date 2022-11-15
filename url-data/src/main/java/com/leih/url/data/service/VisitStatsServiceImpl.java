package com.leih.url.data.service;

import com.leih.url.common.intercepter.LoginInterceptor;
import com.leih.url.data.controller.request.VisitRecordPageRequest;
import com.leih.url.data.entity.VisitStats;
import com.leih.url.data.manager.VisitStatsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VisitStatsServiceImpl implements VisitStatsService{
    @Autowired
    private VisitStatsManager visitStatsManager;
    @Override
    public Map<String, Object> paginateVisitRecord(VisitRecordPageRequest request) {
        Long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
        Map<String, Object> data = new HashMap<>(16);
        String code = request.getCode();
        int page = request.getPage();
        int size = request.getSize();
        int count = visitStatsManager.countAll(accountNo,code);
        int from = (page-1)*size;
        List<VisitStats> list = visitStatsManager.paginateVisitRecord(code,accountNo,from,size);
        data.put("total",count);
        data.put("current_page",page);

        int totalPage=0;
        if(count%size ==0){
            totalPage=count/size;
        }else{
            totalPage=count/size+1;
        }
        data.put("total_page",totalPage);
        data.put("data",list);
        return data;
    }
}
