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
import com.leih.url.link.entity.Link;
import com.leih.url.link.entity.LinkGroup;
import com.leih.url.link.manager.DomainManager;
import com.leih.url.link.manager.LinkGroupManager;
import com.leih.url.link.manager.ShortLinkManager;
import com.leih.url.link.service.ShortLinkService;
import com.leih.url.link.vo.LinkVo;
import io.jsonwebtoken.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ShortLinkServiceImpl implements ShortLinkService {

  @Autowired private ShortLinkManager shortLinkManager;
  @Autowired private RabbitMQConfig rabbitMQConfig;
  @Autowired private RabbitTemplate rabbitTemplate;
  @Autowired private DomainManager domainManager;
  @Autowired private LinkGroupManager linkGroupManager;
  @Autowired private ShortLinkComponent shortLinkComponent;
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

  @Override
  public boolean handleAddShortLink(EventMessage eventMessage) {
    Long accountNo = eventMessage.getAccountNo();
    String eventMessageType = eventMessage.getEventMessageType();
    //get request
    ShortLinkAddRequest shortLinkAddRequest =
        JsonUtil.json2Obj(eventMessage.getContent(), ShortLinkAddRequest.class);
    Domain domain = checkDomain(shortLinkAddRequest.getDomainType(), shortLinkAddRequest.getDomainId(), accountNo);
    LinkGroup linkGroup = checkLinkGroup(shortLinkAddRequest.getGroupId(), accountNo);
    String originalUrlDigest = CommonUtil.MD5(shortLinkAddRequest.getOriginalUrl());
    String shortLinkCode = shortLinkComponent.createShortLink(shortLinkAddRequest.getOriginalUrl());
    Link shortLink = Link.builder().accountNo(accountNo)
            .code(shortLinkCode)
            .name(shortLinkAddRequest.getName())
            .originalUrl(shortLinkAddRequest.getOriginalUrl())
            .domain(domain.getValue())
            .groupId(linkGroup.getId())
            .expired(shortLinkAddRequest.getExpired())
            .sign(originalUrlDigest)
            .state(ShortLinkStateEnum.ACTIVATED.name())
            .delete(0)
            .build();
    return shortLinkManager.addShortLink(shortLink);
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

  private LinkGroup checkLinkGroup(Long groupId,Long accountNo){
    LinkGroup group = linkGroupManager.getGroup(accountNo, groupId);
    Assert.notNull(group,"Invalid group name");
    return group;
  }
}
