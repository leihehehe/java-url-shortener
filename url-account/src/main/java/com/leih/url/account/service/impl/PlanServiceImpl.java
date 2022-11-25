package com.leih.url.account.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leih.url.account.config.RabbitMQConfig;
import com.leih.url.account.controller.request.PlanPageRequest;
import com.leih.url.account.controller.request.UsePlanRequest;
import com.leih.url.account.entity.Plan;
import com.leih.url.account.entity.PlanTask;
import com.leih.url.account.feign.ProductFeignService;
import com.leih.url.account.feign.ShortLinkFeignService;
import com.leih.url.account.manager.PlanManager;
import com.leih.url.account.manager.PlanTaskManager;
import com.leih.url.account.service.PlanService;
import com.leih.url.account.vo.PlanVo;
import com.leih.url.account.vo.ProductVo;
import com.leih.url.account.vo.UserPlanVo;
import com.leih.url.common.constant.RedisKey;
import com.leih.url.common.enums.BizCodeEnum;
import com.leih.url.common.enums.EventMessageTypeEnum;
import com.leih.url.common.enums.TaskStateEnum;
import com.leih.url.common.exception.BizException;
import com.leih.url.common.intercepter.LoginInterceptor;
import com.leih.url.common.model.EventMessage;
import com.leih.url.common.util.JsonData;
import com.leih.url.common.util.JsonUtil;
import com.leih.url.common.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PlanServiceImpl implements PlanService {
  @Autowired RedisTemplate<Object, Object> redisTemplate;
  @Autowired private PlanManager planManager;
  @Autowired private ProductFeignService productFeignService;

  /**
   * Handle plan messages
   *
   * @param eventMessage
   */
  @Override
  @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
  public void handlePlanMessage(EventMessage eventMessage) {
    String messageType = eventMessage.getEventMessageType();
    Long accountNo = eventMessage.getAccountNo();
    if (EventMessageTypeEnum.PRODUCT_ORDER_PAID.name().equalsIgnoreCase(messageType)) {
      // order has been paid
      String content = eventMessage.getContent();
      Map<String, Object> map = JsonUtil.json2Obj(content, Map.class);
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
        //delete the original key
        String totalAvailableTimesKey = String.format(RedisKey.PLAN_TOTAL_AVAILABLE_TIMES, accountNo);
        redisTemplate.delete(totalAvailableTimesKey);
      } else {
        log.error("Failed to add a plan:{}", plan);
      }
    } else if (EventMessageTypeEnum.PLAN_FREE_NEW_ACCOUNT.name().equalsIgnoreCase(messageType)) {
      Long productId = Long.valueOf(eventMessage.getBizId());
      JsonData jsonData = productFeignService.getProductDetail(productId);
      try {
        ProductVo productVo = jsonData.getData(new TypeReference<ProductVo>() {});
        LocalDateTime localDateTime = TimeUtil.atEndOfDay(new Date());
        Timestamp expiredDateTime = Timestamp.valueOf(localDateTime);

        Plan initPlan =
            Plan.builder()
                .accountNo(accountNo)
                .dayLimit(productVo.getDayTimes())
                .dayUsed(0)
                .totalLimit(productVo.getTotalTimes())
                .pluginType(productVo.getPluginType())
                .level(productVo.getLevel())
                .productId(productVo.getId())
                .orderNo("free_init")
                .expiredDate(expiredDateTime)
                .build();
        planManager.addPlan(initPlan);
      } catch (Exception e) {
        log.error("Failed to init free plans for the new account:{}",e.getMessage());
        throw new BizException(BizCodeEnum.MQ_CONSUMER_EXCEPTION);
      }
    }else if (EventMessageTypeEnum.LINK_CHECK_IF_CREATED.name().equalsIgnoreCase(eventMessage.getEventMessageType())){
      //this is a delayed message to check if short link has already been created
      Long planTaskId = Long.valueOf(eventMessage.getBizId());
      PlanTask planTask = planTaskManager.findByIdAndAccountNo(planTaskId, accountNo);
      if(planTask!=null && planTask.getLockState().equalsIgnoreCase(TaskStateEnum.LOCK.name())){
        JsonData jsonData = shortLinkFeignService.checkShortLink(planTask.getBizId());
        if(jsonData.getCode()!=0){
          log.error("Failed to create the short link, roll back the plan");
          String taskCreateDate = TimeUtil.format(planTask.getGmtCreate(), "yyyy-MM-dd");
          planManager.restoreUsedTimes(planTask.getPlanId(),accountNo,1,taskCreateDate);
          // delete the key to re-calculate the plans
          String totalAvailableTimesKey = String.format(RedisKey.PLAN_TOTAL_AVAILABLE_TIMES, accountNo);
          redisTemplate.delete(totalAvailableTimesKey);
        }
        planTaskManager.deleteByIdAndAccountNo(planTaskId,accountNo);
      }
    }
  }
@Autowired
  ShortLinkFeignService shortLinkFeignService;
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
        plans.getContent().stream().map(this::convertPlanToVo).collect(Collectors.toList()));
    return pageInfo;
  }

  @Override
  public PlanVo getPlanDetail(Long planId) {
    Long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
    Plan plan = planManager.findByIdAndAccountNo(planId, accountNo);
    if (plan != null) {
      return convertPlanToVo(plan);
    }
    return null;
  }
  @Autowired
  PlanTaskManager planTaskManager;
  @Autowired
  RabbitMQConfig rabbitMQConfig;
  @Autowired
  RabbitTemplate rabbitTemplate;
  /**
   * Choose which plan to use
   *
   * @param request
   * @return
   */
  @Override
  @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
  public JsonData usePlan(UsePlanRequest request) {
    Long accountNo = request.getAccountNo();
    UserPlanVo userPlanVo = processPlanList(accountNo);
    log.info(
        "available times for today:{}, current plan:{}",
        userPlanVo.getDayTotalLeftTimes(),
        userPlanVo.getCurrentPlan());
    if (userPlanVo.getCurrentPlan() == null) {
      return JsonData.buildResult(BizCodeEnum.PLAN_REDUCE_FAIL);
    }
    log.info("plans waiting to be updated: {}", userPlanVo.getNotUpdatedPlanIds());
    //update plans
    if (userPlanVo.getNotUpdatedPlanIds().size() > 0) {
      planManager.batchUpdateUsedTimesToZero(accountNo, userPlanVo.getNotUpdatedPlanIds());
    }
    //create plan task
    PlanTask planTask = PlanTask.builder().accountNo(accountNo)
            .bizId(request.getBizId())
            .useTimes(1)
            .planId(userPlanVo.getCurrentPlan().getId())
            .lockState(TaskStateEnum.LOCK.name())
            .build();
    planTaskManager.addPlanTask(planTask);
    // update first then deduct the current used plan
    if (!planManager.addDayUsedTimes(userPlanVo.getCurrentPlan().getId(), accountNo, 1)) {
      throw new BizException(BizCodeEnum.PLAN_REDUCE_FAIL);
    }
    long secondsLeftInTheDay = TimeUtil.getSecondsLeftInTheDay(new Date());
    String totalAvailableTimesKey = String.format(RedisKey.PLAN_TOTAL_AVAILABLE_TIMES, accountNo);
    //store left available times to redis
    redisTemplate.opsForValue().set(totalAvailableTimesKey,userPlanVo.getDayTotalLeftTimes()-1,secondsLeftInTheDay, TimeUnit.SECONDS);

    //send the delayed message to check if the short link has already been created successfully.
    EventMessage eventMessage = EventMessage.builder().accountNo(accountNo).bizId(String.valueOf(planTask.getId()))
            .eventMessageType(EventMessageTypeEnum.LINK_CHECK_IF_CREATED.name()).build();
    rabbitTemplate.convertAndSend(rabbitMQConfig.getPlanEventExchange(),rabbitMQConfig.getPlanRestoreDelayRoutingKey(),eventMessage);

    return JsonData.buildSuccess();
  }

  @Override
  public boolean deleteExpiredPlans() {
    return planManager.deleteExpiredPlans();
  }

  /**
   * Process plan list
   *
   * @param accountNo
   * @return
   */
  public UserPlanVo processPlanList(Long accountNo) {
    // all available plans
    List<Plan> availablePlans = planManager.findAvailablePlans(accountNo);
    if (availablePlans == null || availablePlans.size() == 0) {
      throw new BizException(BizCodeEnum.PLAN_EXCEPTION);
    }
    Integer dayTotalLeftTimes = 0;
    Plan currentPlan = null;
    List<Long> notUpdatedPlanIds = new ArrayList<>();
    String today = TimeUtil.format(new Date(), "yyyy-MM-dd");
    // calculate the total times left and assign the plan to be consumed
    for (Plan plan : availablePlans) {
      String planUpdateDate = TimeUtil.format(plan.getGmtModified(), "yyyy-MM-dd");
      if (planUpdateDate.equalsIgnoreCase(today)) {
        // has updated today
        int dayLeftTimes = plan.getDayLimit() - plan.getDayUsed();
        dayTotalLeftTimes = dayTotalLeftTimes + dayLeftTimes;
        // choose which plan to deduct
        if (dayLeftTimes > 0 && currentPlan == null) {
          currentPlan = plan;
        }
      } else {
        // has not updated today
        dayTotalLeftTimes = dayTotalLeftTimes + plan.getDayLimit();
        notUpdatedPlanIds.add(plan.getId());
        if (currentPlan == null) {
          currentPlan = plan;
        }
      }
    }
    UserPlanVo userPlanVo = new UserPlanVo(dayTotalLeftTimes, currentPlan, notUpdatedPlanIds);
    return userPlanVo;
  }

  public PlanVo convertPlanToVo(Plan plan) {
    PlanVo planVo = new PlanVo();
    BeanUtils.copyProperties(plan, planVo);
    String todayStr = TimeUtil.format(new Date(),"yyyy-MM-dd");
    String trafficUpdateStr = TimeUtil.format(plan.getGmtModified(),"yyyy-MM-dd");
    if(!todayStr.equalsIgnoreCase(trafficUpdateStr)){
      planVo.setDayUsed(0);
    }
    return planVo;
  }
}
