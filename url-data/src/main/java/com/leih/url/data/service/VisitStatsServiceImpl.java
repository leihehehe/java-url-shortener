package com.leih.url.data.service;

import com.leih.url.common.enums.DateTimeFieldEnum;
import com.leih.url.common.intercepter.LoginInterceptor;
import com.leih.url.data.controller.request.RegionQueryRequest;
import com.leih.url.data.controller.request.VisitRecordPageRequest;
import com.leih.url.data.controller.request.VisitTrendQueryRequest;
import com.leih.url.data.entity.VisitStats;
import com.leih.url.data.manager.VisitStatsManager;
import com.leih.url.data.vo.VisitStatsVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VisitStatsServiceImpl implements VisitStatsService {
  @Autowired private VisitStatsManager visitStatsManager;

  @Override
  public Map<String, Object> paginateVisitRecord(VisitRecordPageRequest request) {
    Long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
    Map<String, Object> data = new HashMap<>(16);
    String code = request.getCode();
    int page = request.getPage();
    int size = request.getSize();
    int count = visitStatsManager.countAll(accountNo, code);
    int from = (page - 1) * size;
    List<VisitStats> list = visitStatsManager.paginateVisitRecord(code, accountNo, from, size);
    List<VisitStatsVo> visitStatsVos =
        list.stream().map(this::convertVisitStatsToVo).collect(Collectors.toList());
    data.put("total", count);
    data.put("current_page", page);

    int totalPage = 0;
    if (count % size == 0) {
      totalPage = count / size;
    } else {
      totalPage = count / size + 1;
    }
    data.put("total_page", totalPage);
    data.put("data", visitStatsVos);
    return data;
  }

  @Override
  public List<VisitStatsVo> queryRegion(RegionQueryRequest request) {
    Long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
    List<VisitStats> visitStatsList =
        visitStatsManager.queryRegion(
            request.getCode(), request.getStartTime(), request.getEndTime(), accountNo);
    List<VisitStatsVo> visitStatsVoList =
        visitStatsList.stream().map(this::convertVisitStatsToVo).collect(Collectors.toList());
    return visitStatsVoList;
  }

  @Override
  public List<VisitStatsVo> queryVisitTrend(VisitTrendQueryRequest request) {
    Long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
    String code = request.getCode();
    String type = request.getType();
    String startTime = request.getStartTime();
    String endTime = request.getEndTime();
    List<VisitStats> visitStatsList = null;
    if (DateTimeFieldEnum.DAY.name().equalsIgnoreCase(type)) {
      visitStatsList = visitStatsManager.queryDayVisitTrend(accountNo, code, startTime, endTime);
    } else if (DateTimeFieldEnum.MINUTE.name().equalsIgnoreCase(type)) {

    } else if (DateTimeFieldEnum.WEEK.name().equalsIgnoreCase(type)) {

    }else if (DateTimeFieldEnum.HOUR.name().equalsIgnoreCase(type)) {

    }
    List<VisitStatsVo> visitStatsVoList = visitStatsList.stream().map(this::convertVisitStatsToVo).collect(Collectors.toList());
    return visitStatsVoList;
  }

  public VisitStatsVo convertVisitStatsToVo(VisitStats visitStats) {
    VisitStatsVo visitStatsVo = new VisitStatsVo();
    BeanUtils.copyProperties(visitStats, visitStatsVo);
    return visitStatsVo;
  }
}
