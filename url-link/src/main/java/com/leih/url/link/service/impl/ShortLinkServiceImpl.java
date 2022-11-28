package com.leih.url.link.service.impl;

import com.ctc.wstx.util.URLUtil;
import com.leih.url.common.constant.RedisKey;
import com.leih.url.common.enums.BizCodeEnum;
import com.leih.url.common.enums.DomainTypeEnum;
import com.leih.url.common.enums.EventMessageTypeEnum;
import com.leih.url.common.enums.ShortLinkStateEnum;
import com.leih.url.common.intercepter.LoginInterceptor;
import com.leih.url.common.model.EventMessage;
import com.leih.url.common.util.CommonUtil;
import com.leih.url.common.util.IdUtil;
import com.leih.url.common.util.JsonData;
import com.leih.url.common.util.JsonUtil;
import com.leih.url.link.component.ShortLinkComponent;
import com.leih.url.link.config.RabbitMQConfig;
import com.leih.url.link.controller.request.*;
import com.leih.url.link.entity.Domain;
import com.leih.url.link.entity.GroupLinkMapping;
import com.leih.url.link.entity.Link;
import com.leih.url.link.entity.LinkGroup;
import com.leih.url.link.feign.PlanFeignService;
import com.leih.url.link.manager.DomainManager;
import com.leih.url.link.manager.GroupLinkMappingManager;
import com.leih.url.link.manager.LinkGroupManager;
import com.leih.url.link.manager.ShortLinkManager;
import com.leih.url.link.service.ShortLinkService;
import com.leih.url.link.vo.LinkVo;
import io.jsonwebtoken.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Arrays;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ShortLinkServiceImpl implements ShortLinkService {

  @Autowired private ShortLinkManager shortLinkManager;
  @Autowired private RabbitMQConfig rabbitMQConfig;
  @Autowired private RabbitTemplate rabbitTemplate;
  @Autowired private DomainManager domainManager;
  @Autowired private LinkGroupManager linkGroupManager;
  @Autowired private GroupLinkMappingManager groupLinkMappingManager;
  @Autowired private ShortLinkComponent shortLinkComponent;
  @Autowired private RedisTemplate<Object, Object> redisTemplate;
  @Autowired private PlanFeignService planFeignService;

  @Override
  public LinkVo parseShortLinkCode(String shortLinkCode) {
    Link shortLink = shortLinkManager.findShortLinkByCode(shortLinkCode);
    if (shortLink == null) {
      log.error("No short link was found, short link code: {}", shortLinkCode);
      return null;
    }
    LinkVo linkVo = new LinkVo();
    BeanUtils.copyProperties(shortLink, linkVo);
    return linkVo;
  }

  @Override
  public JsonData createShortLink(ShortLinkAddRequest request) {
    Long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
    // check the plan cache
    String key = String.format(RedisKey.PLAN_TOTAL_AVAILABLE_TIMES, accountNo);
    String script =
        "if redis.call('get',KEYS[1]) then return redis.call('decr',KEYS[1]) else return 0 end";
    Long leftTimes =
        redisTemplate.execute(new DefaultRedisScript<>(script, Long.class), List.of(key));
    log.info("Available times:{}", leftTimes);
    if (leftTimes >= 0) {
      // avoid url conflicts
      if(!StringUtils.startsWithAny(request.getOriginalUrl(),"https://","http://")){
        return JsonData.buildResult(BizCodeEnum.ORIGINAL_URL_NOT_CORRECT);
      }
      String newOriginalUrl = CommonUtil.addUrlPrefix(request.getOriginalUrl());
      request.setOriginalUrl(newOriginalUrl);
      EventMessage eventMessage =
          EventMessage.builder()
              .accountNo(accountNo)
              .content(JsonUtil.obj2Json(request))
              .messageId(IdUtil.generateSnowFlakeId().toString())
              .eventMessageType(EventMessageTypeEnum.SHORT_LINK_ADD.name())
              .build();
      rabbitTemplate.convertAndSend(
          rabbitMQConfig.getShortLinkEventExchange(),
          rabbitMQConfig.getShortLinkAddRoutingKey(),
          eventMessage);
      return JsonData.buildSuccess();
    } else {
      return JsonData.buildResult(BizCodeEnum.PLAN_REDUCE_FAIL);
    }
  }
  /**
   * Add short link in terms of event message type Add short link to link/mapping tables
   *
   * @param eventMessage
   * @return
   */
  @Override
  public boolean handleAddShortLink(EventMessage eventMessage) {
    Long accountNo = eventMessage.getAccountNo();
    String eventMessageType = eventMessage.getEventMessageType();
    // get request
    ShortLinkAddRequest shortLinkAddRequest =
        JsonUtil.json2Obj(eventMessage.getContent(), ShortLinkAddRequest.class);
    // get domain
    Domain domain =
        checkDomain(
            shortLinkAddRequest.getDomainType(), shortLinkAddRequest.getDomainId(), accountNo);
    // get group
    LinkGroup linkGroup = checkLinkGroup(shortLinkAddRequest.getGroupId(), accountNo);
    // get digest of the original url
    String originalUrlDigest = CommonUtil.MD5(shortLinkAddRequest.getOriginalUrl());
    String shortLinkCode = shortLinkComponent.createShortLink(shortLinkAddRequest.getOriginalUrl());
    // add a distributed lock for the following action
    String script =
        // check if the key(short link code) exist in the redis
        "if redis.call('EXISTS',KEYS[1])==0 then redis.call('set',KEYS[1],ARGV[1]); "
            // if it does not exist, set key and expire
            + "redis.call('expire',KEYS[1],ARGV[2]); return 1;"
            // if it does exist, and the accountNo is the same, return 2
            + " elseif redis.call('get',KEYS[1]) == ARGV[1] then return 2;"
            + " else return 0; end;";
    String shortLinkCodeKey = String.format(RedisKey.SHORT_LINK_CODE_KEY, shortLinkCode);
    Long result =
        redisTemplate.execute(
            new DefaultRedisScript<>(script, Long.class),
            List.of(shortLinkCodeKey),
            accountNo,
            100);
    boolean success = true;
    if (result > 0) {
      // successfully add a lock
      if (EventMessageTypeEnum.SHORT_LINK_ADD_LINK.name().equalsIgnoreCase(eventMessageType)) {
        Link shortLinkInDB = shortLinkManager.findShortLinkByCode(shortLinkCode);
        if (shortLinkInDB == null) {
          // short link code is not used in the database
          // check if the user has enough number of times a short link can be created

          if (usePlan(eventMessage, shortLinkCode)) {
            // another listener has already checked this operation is valid
            Link shortLink =
                Link.builder()
                    .accountNo(accountNo)
                    .code(shortLinkCode)
                    .name(shortLinkAddRequest.getName())
                    .originalUrl(shortLinkAddRequest.getOriginalUrl())
                    .domain(domain.getValue())
                    .groupId(linkGroup.getId())
                    .expired(shortLinkAddRequest.getExpired())
                    .sign(originalUrlDigest)
                    .state(ShortLinkStateEnum.ACTIVATED.name())
                    .del(0)
                    .build();
            return shortLinkManager.addShortLink(shortLink);
          } else {
            log.error(
                "Failed to use plan - Insufficient number of times a short link can be created - ShortLink");
            //empty the key to make next query available
            String totalAvailableTimesKey = String.format(RedisKey.PLAN_TOTAL_AVAILABLE_TIMES,
                    eventMessage.getAccountNo());
            redisTemplate.delete(totalAvailableTimesKey);
            //send a message to delete group mapping
            ShortLinkDeleteRequest shortLinkDeleteRequest = new ShortLinkDeleteRequest();
            shortLinkDeleteRequest.setCode(shortLinkCode);
            shortLinkDeleteRequest.setGroupId(linkGroup.getId());
            EventMessage checkMessage = EventMessage.builder().eventMessageType(EventMessageTypeEnum.LINK_CHECK_IF_CREATED.name())
                    .accountNo(accountNo)
                    .bizId(shortLinkCode)
                    .content(JsonUtil.obj2Json(shortLinkDeleteRequest))
                    .build();
            rabbitTemplate.convertAndSend(rabbitMQConfig.getShortLinkEventExchange(),rabbitMQConfig.getShortLinkCheckDelayRoutingKey(),checkMessage);
            return false;
          }

        } else {
          log.error("Short link exists in the link tables:{}", eventMessage);
          success = false;
        }
      } else if (EventMessageTypeEnum.SHORT_LINK_ADD_MAPPING
          .name()
          .equalsIgnoreCase(eventMessageType)) {
        GroupLinkMapping groupLinkMappingInDB =
            groupLinkMappingManager.findShortLinkByCode(
                shortLinkCode, linkGroup.getId(), accountNo);
        // check if group link mapping exists
        if (groupLinkMappingInDB == null) {
          // check if the user has enough number of times a short link can be created
            // another listener has already checked this operation is valid
            GroupLinkMapping groupLinkMapping =
                GroupLinkMapping.builder()
                    .accountNo(accountNo)
                    .code(shortLinkCode)
                    .name(shortLinkAddRequest.getName())
                    .originalUrl(shortLinkAddRequest.getOriginalUrl())
                    .domain(domain.getValue())
                    .groupId(linkGroup.getId())
                    .expired(shortLinkAddRequest.getExpired())
                    .sign(originalUrlDigest)
                    .state(ShortLinkStateEnum.ACTIVATED.name())
                    .del(0)
                    .build();
            return groupLinkMappingManager.addShortLink(groupLinkMapping);

        } else {
          log.error("Group link mapping exists in the mapping tables:{}", eventMessage);
          success = false;
        }
      }
    } else {
      // failed
      log.error("Filed to add lock:{}", eventMessage);

      try {
        TimeUnit.MILLISECONDS.sleep(100);
      } catch (InterruptedException e) {
      }
      success = false;
    }
    if (!success) {
      String newOriginalUrl = CommonUtil.addUrlPrefixVersion(shortLinkAddRequest.getOriginalUrl());
      shortLinkAddRequest.setOriginalUrl(newOriginalUrl);
      eventMessage.setContent(JsonUtil.obj2Json(shortLinkAddRequest));
      handleAddShortLink(eventMessage);
    }
    return false;
  }

  /**
   * Deduct plan
   *
   * @param eventMessage
   * @param shortLinkCode
   * @return
   */
  private boolean usePlan(EventMessage eventMessage, String shortLinkCode) {
    UsePlanRequest request =
        UsePlanRequest.builder()
            .accountNo(eventMessage.getAccountNo())
            .bizId(shortLinkCode)
            .build();
    JsonData result = planFeignService.usePlan(request);
    if (result.getCode() != 0) {
      //      String totalAvailableTimesKey = String.format(RedisKey.PLAN_TOTAL_AVAILABLE_TIMES,
      // eventMessage.getAccountNo());
      //      redisTemplate.delete(totalAvailableTimesKey);
      log.error("Failed to deduct the plan, insufficient amount");
      return false;
    }
    return true;
  }

  /**
   * Handle update requests
   *
   * @param eventMessage
   * @return
   */
  public boolean handleUpdateShortLink(EventMessage eventMessage) {
    Long accountNo = eventMessage.getAccountNo();
    String eventMessageType = eventMessage.getEventMessageType();
    ShortLinkUpdateRequest request =
        JsonUtil.json2Obj(eventMessage.getContent(), ShortLinkUpdateRequest.class);
    Domain domain = checkDomain(request.getDomainType(), request.getDomainId(), accountNo);
    if (EventMessageTypeEnum.SHORT_LINK_UPDATE_LINK.name().equalsIgnoreCase(eventMessageType)) {
      Link updatedLink =
          Link.builder()
              .code(request.getCode())
              .name(request.getName())
              .domain(domain.getValue())
              .accountNo(accountNo)
              .build();
      log.info("Updating short link for short_link table");
      return shortLinkManager.updateShortLink(updatedLink);
    } else if (EventMessageTypeEnum.SHORT_LINK_UPDATE_MAPPING
        .name()
        .equalsIgnoreCase(eventMessageType)) {
      GroupLinkMapping updatedLinkMapping =
          GroupLinkMapping.builder()
              .id(request.getMappingId())
              .groupId(request.getGroupId())
              .accountNo(accountNo)
              .name(request.getName())
              .domain(domain.getValue())
              .build();
      log.info("Updating short link for group_link_mapping table");
      return groupLinkMappingManager.updateGroupMapping(updatedLinkMapping);
    }
    return false;
  }

  @Override
  public boolean handleDeleteShortLink(EventMessage eventMessage) {
    Long accountNo = eventMessage.getAccountNo();
    String eventMessageType = eventMessage.getEventMessageType();
    ShortLinkDeleteRequest request =
        JsonUtil.json2Obj(eventMessage.getContent(), ShortLinkDeleteRequest.class);
    if (EventMessageTypeEnum.SHORT_LINK_DELETE_LINK.name().equalsIgnoreCase(eventMessageType)) {
      Link shortLink = Link.builder().code(request.getCode()).accountNo(accountNo).build();
      return shortLinkManager.deleteShortLink(shortLink);
    } else if (EventMessageTypeEnum.SHORT_LINK_DELETE_MAPPING
        .name()
        .equalsIgnoreCase(eventMessageType)) {
      GroupLinkMapping groupLinkMapping =
          GroupLinkMapping.builder()
              .groupId(request.getGroupId())
              .accountNo(accountNo)
              .id(request.getMappingId())
              .build();
      return groupLinkMappingManager.deleteShortLink(groupLinkMapping);
    }
    return false;
  }

  @Override
  public boolean deleteShortLinkMappingByCode(EventMessage eventMessage) {
    ShortLinkDeleteRequest request =
            JsonUtil.json2Obj(eventMessage.getContent(), ShortLinkDeleteRequest.class);
    GroupLinkMapping groupLinkMapping = GroupLinkMapping.builder().groupId(request.getGroupId())
            .code(request.getCode())
            .accountNo(eventMessage.getAccountNo()).build();
    return groupLinkMappingManager.fullyDeleteShortLinkByCode(groupLinkMapping);
  }

  /**
   * Query from the table group_link_mapping
   *
   * @param request
   * @return
   */
  @Override
  public Map<String, Object> pageByGroupId(ShortLinkPageRequest request) {
    Long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
    Map<String, Object> result =
        groupLinkMappingManager.pageShortLinkByGroupId(
            request.getPage(), request.getSize(), accountNo, request.getGroupId());
    return result;
  }

  @Override
  public JsonData deleteShortLink(ShortLinkDeleteRequest request) {
    Long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
    EventMessage eventMessage =
        EventMessage.builder()
            .accountNo(accountNo)
            .content(JsonUtil.obj2Json(request))
            .messageId(IdUtil.generateSnowFlakeId().toString())
            .eventMessageType(EventMessageTypeEnum.SHORT_LINK_DELETE.name())
            .build();
    rabbitTemplate.convertAndSend(
        rabbitMQConfig.getShortLinkEventExchange(),
        rabbitMQConfig.getShortLinkDelRoutingKey(),
        eventMessage);
    return JsonData.buildSuccess();
  }

  @Override
  public JsonData updateShortLink(ShortLinkUpdateRequest request) {
    Long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
    EventMessage eventMessage =
        EventMessage.builder()
            .accountNo(accountNo)
            .content(JsonUtil.obj2Json(request))
            .messageId(IdUtil.generateSnowFlakeId().toString())
            .eventMessageType(EventMessageTypeEnum.SHORT_LINK_UPDATE.name())
            .build();
    rabbitTemplate.convertAndSend(
        rabbitMQConfig.getShortLinkEventExchange(),
        rabbitMQConfig.getShortLinkUpdateRoutingKey(),
        eventMessage);
    return JsonData.buildSuccess();
  }

  /**
   * Check if the domain is valid
   *
   * @param domainType
   * @param domainId
   * @param accountNo
   * @return
   */
  private Domain checkDomain(String domainType, Long domainId, Long accountNo) {
    Domain domain;
    if (DomainTypeEnum.CUSTOM.name().equalsIgnoreCase(domainType)) {
      domain = domainManager.findById(domainId, accountNo);
    } else {
      domain = domainManager.findByDomainTypeEnum(domainId, DomainTypeEnum.OFFICIAL);
    }
    Assert.notNull(domain, "Invalid domain");
    return domain;
  }

  private LinkGroup checkLinkGroup(Long groupId, Long accountNo) {
    LinkGroup group = linkGroupManager.getGroup(accountNo, groupId);
    Assert.notNull(group, "Invalid group name");
    return group;
  }
}
