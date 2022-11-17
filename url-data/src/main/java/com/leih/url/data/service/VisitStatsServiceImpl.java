package com.leih.url.data.service;

import com.leih.url.common.enums.DateTimeFieldEnum;
import com.leih.url.common.enums.DeviceEnum;
import com.leih.url.common.intercepter.LoginInterceptor;
import com.leih.url.data.controller.request.*;
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
    return visitStatsList.stream().map(this::convertVisitStatsToVo).collect(Collectors.toList());
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
      visitStatsList = visitStatsManager.queryVisitTrend(accountNo, code, startTime, endTime,DateTimeFieldEnum.DAY);
    } else if (DateTimeFieldEnum.MINUTE.name().equalsIgnoreCase(type)) {
      visitStatsList = visitStatsManager.queryVisitTrend(accountNo, code, startTime, endTime,DateTimeFieldEnum.MINUTE);
    } else if (DateTimeFieldEnum.WEEK.name().equalsIgnoreCase(type)) {
      visitStatsList = visitStatsManager.queryVisitTrend(accountNo, code, startTime, endTime,DateTimeFieldEnum.WEEK);

    }else if (DateTimeFieldEnum.HOUR.name().equalsIgnoreCase(type)) {
      visitStatsList = visitStatsManager.queryVisitTrend(accountNo, code, startTime, endTime,DateTimeFieldEnum.HOUR);
    }
    return visitStatsList.stream().map(this::convertVisitStatsToVo).collect(Collectors.toList());
  }

  @Override
  public List<VisitStatsVo> queryFrequentSource(FrequentSourceRequest request) {
    Long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
    String code = request.getCode();
    String startTime = request.getStartTime();
    String endTime = request.getEndTime();
    List<VisitStats> visitStatsList = visitStatsManager.queryFrequentSource(accountNo, code, startTime, endTime,10);
    return visitStatsList.stream().map(this::convertVisitStatsToVo).collect(Collectors.toList());
  }

  @Override
  public  Map<String,List<VisitStatsVo>> queryDeviceInfo(DeviceQueryRequest request) {
    Long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
    String code = request.getCode();
    String startTime = request.getStartTime();
    String endTime = request.getEndTime();
    List<VisitStats> osList = visitStatsManager.queryDeviceInfo(accountNo, code, startTime, endTime,DeviceEnum.OS);
    List<VisitStats> deviceList = visitStatsManager.queryDeviceInfo(accountNo, code, startTime, endTime,DeviceEnum.DEVICE_TYPE);
    List<VisitStats> browserList = visitStatsManager.queryDeviceInfo(accountNo, code, startTime, endTime,DeviceEnum.BROWSER_NAME);
    List<VisitStatsVo> osVisitStatsVoList = osList.stream().map(this::convertVisitStatsToVo).toList();
    List<VisitStatsVo> browserVisitStatsVoList = browserList.stream().map(this::convertVisitStatsToVo).toList();
    List<VisitStatsVo> deviceTypeVisitStatsVoList = deviceList.stream().map(this::convertVisitStatsToVo).toList();
    Map<String,List<VisitStatsVo>> map = new HashMap<>(3);
    map.put("os",osVisitStatsVoList);
    map.put("browser_name",browserVisitStatsVoList);
    map.put("device_name",deviceTypeVisitStatsVoList);
    return map;
  }

  public VisitStatsVo convertVisitStatsToVo(VisitStats visitStats) {
    VisitStatsVo visitStatsVo = new VisitStatsVo();
    BeanUtils.copyProperties(visitStats, visitStatsVo);
    return visitStatsVo;
  }
}
