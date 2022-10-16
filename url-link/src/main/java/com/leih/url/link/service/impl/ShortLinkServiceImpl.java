package com.leih.url.link.service.impl;

import com.leih.url.common.enums.DomainTypeEnum;
import com.leih.url.common.enums.EventMessageType;
import com.leih.url.common.enums.ShortLinkStateEnum;
import com.leih.url.common.intercepter.LoginInterceptor;
import com.leih.url.common.model.EventMessage;
import com.leih.url.common.util.CommonUtil;
import com.leih.url.common.util.IdUtil;
import com.leih.url.common.util.JsonData;
import com.leih.url.common.util.JsonUtil;
import com.leih.url.link.component.ShortLinkComponent;
import com.leih.url.link.config.RabbitMQConfig;
import com.leih.url.link.controller.request.ShortLinkAddRequest;
import com.leih.url.link.entity.Domain;
import com.leih.url.link.entity.GroupLinkMapping;
import com.leih.url.link.entity.Link;
import com.leih.url.link.entity.LinkGroup;
import com.leih.url.link.manager.DomainManager;
import com.leih.url.link.manager.GroupLinkMappingManager;
import com.leih.url.link.manager.LinkGroupManager;
import com.leih.url.link.manager.ShortLinkManager;
import com.leih.url.link.service.ShortLinkService;
import com.leih.url.link.vo.LinkVo;
import io.jsonwebtoken.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.List;
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
    // avoid url conflicts
    String newOriginalUrl = CommonUtil.addUrlPrefix(request.getOriginalUrl());
    request.setOriginalUrl(newOriginalUrl);
    EventMessage eventMessage =
        EventMessage.builder()
            .accountNo(accountNo)
            .content(JsonUtil.obj2Json(request))
            .messageId(IdUtil.generateSnowFlakeId().toString())
            .eventMessageType(EventMessageType.SHORT_LINK_ADD.name())
            .build();
    rabbitTemplate.convertAndSend(
        rabbitMQConfig.getShortLinkEventExchange(),
        rabbitMQConfig.getShortLinkAddRoutingKey(),
        eventMessage);
    return JsonData.buildSuccess();
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
    Long result =
        redisTemplate.execute(
            new DefaultRedisScript<>(script, Long.class), List.of(shortLinkCode), accountNo, 100);
    boolean success = true;
    if (result > 0) {
      // successfully add a lock
      if (EventMessageType.SHORT_LINK_ADD_LINK.name().equalsIgnoreCase(eventMessageType)) {
        Link shortLinkInDB = shortLinkManager.findShortLinkByCode(shortLinkCode);
        if (shortLinkInDB == null) {
          // short link code is not used in the database
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
          log.error("Short link exists in the link tables:{}", eventMessage);
          success = false;
        }
      } else if (EventMessageType.SHORT_LINK_ADD_MAPPING
          .name()
          .equalsIgnoreCase(eventMessageType)) {
        GroupLinkMapping groupLinkMappingInDB =
            groupLinkMappingManager.findShortLinkByCode(
                shortLinkCode, linkGroup.getId(), accountNo);
        if (groupLinkMappingInDB == null) {
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
