package com.leih.url.account.service.impl;

import com.leih.url.account.controller.request.PlanPageRequest;
import com.leih.url.account.entity.Plan;
import com.leih.url.account.manager.PlanManager;
import com.leih.url.account.service.PlanService;
import com.leih.url.account.vo.PlanVo;
import com.leih.url.account.vo.ProductVo;
import com.leih.url.common.enums.EventMessageType;
import com.leih.url.common.intercepter.LoginInterceptor;
import com.leih.url.common.model.EventMessage;
import com.leih.url.common.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PlanServiceImpl implements PlanService {
  @Autowired private PlanManager planManager;

  /**
   * Handle plan messages
   *
   * @param eventMessage
   */
  @Override
  @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
  public void handlePlanMessage(EventMessage eventMessage) {
    String messageType = eventMessage.getEventMessageType();
    if (EventMessageType.PRODUCT_ORDER_PAID.name().equalsIgnoreCase(messageType)) {
      // order has been paid
      String content = eventMessage.getContent();
      Map<String, Object> map = JsonUtil.json2Obj(content, Map.class);
      Long accountNo = (Long) map.get("accountNo");
      String orderNo = (String) map.get("orderNo");
      Integer buyNum = (Integer) map.get("buyNum");
      String productSnapshot = (String) map.get("product");
      ProductVo productVo = JsonUtil.json2Obj(productSnapshot, ProductVo.class);
      log.info("Product info:{}", productVo);

      LocalDateTime expiredDateTime = LocalDateTime.now().plusDays(productVo.getValidDays());
      Timestamp timestamp = Timestamp.valueOf(expiredDateTime);
//      Date date = Date.from(expiredDateTime.atZone(ZoneId.systemDefault()).toInstant());
      Plan plan =
          Plan.builder()
              .accountNo(accountNo)
              .dayLimit(productVo.getDayTimes() * buyNum)
              .dayUsed(0)
              .pluginType(productVo.getPluginType())
              .level(productVo.getLevel())
              .productId(productVo.getId())
              .orderNo(orderNo)
              .expiredDate(timestamp)
              .totalLimit(productVo.getTotalTimes())
              .build();
      boolean result = planManager.addPlan(plan);
      if (result) {
        log.info("Added a plan:{}", plan);
      } else {
        log.error("Failed to add a plan:{}", plan);
      }
    }
  }

  /**
   * Paginate available plans
   *
   * @param request
   * @return
   */
  @Override
  public Map<String, Object> paginateAvailablePlans(PlanPageRequest request) {
    Long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
    Page<Plan> plans =
        planManager.paginateAvailablePlan(request.getPage(), request.getSize(), accountNo);
    Map<String, Object> pageInfo = new HashMap<>(3);
    pageInfo.put("total_records", plans.getTotalElements());
    pageInfo.put("total_pages", plans.getTotalPages());
    pageInfo.put(
        "current_data",
        plans.getContent().stream()
            .map(this::convertPlanToVo)
            .collect(Collectors.toList()));
    return pageInfo;
  }

  @Override
  public PlanVo getPlanDetail(Long planId) {
    Long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
    Plan plan = planManager.findByIdAndAccountNo(planId, accountNo);
    if(plan!=null){
      return convertPlanToVo(plan);
    }
    return null;
  }

  public PlanVo convertPlanToVo(Plan plan) {
    PlanVo productOrderVo = new PlanVo();
    BeanUtils.copyProperties(plan, productOrderVo);
    return productOrderVo;
  }
}
