package com.leih.url.link.service.impl;

import com.leih.url.common.enums.EventMessageType;
import com.leih.url.common.intercepter.LoginInterceptor;
import com.leih.url.common.model.EventMessage;
import com.leih.url.common.util.IdUtil;
import com.leih.url.common.util.JsonData;
import com.leih.url.common.util.JsonUtil;
import com.leih.url.link.config.RabbitMQConfig;
import com.leih.url.link.controller.request.ShortLinkAddRequest;
import com.leih.url.link.entity.Link;
import com.leih.url.link.manager.ShortLinkManager;
import com.leih.url.link.service.ShortLinkService;
import com.leih.url.link.vo.LinkVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ShortLinkServiceImpl implements ShortLinkService {

    @Autowired
    private ShortLinkManager shortLinkManager;
    @Autowired
    private RabbitMQConfig rabbitMQConfig;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Override
    public LinkVo parseShortLinkCode(String shortLinkCode) {
        Link shortLink = shortLinkManager.findShortLinkByCode(shortLinkCode);
        if(shortLink==null){
            log.error("No short link was found, short link code: {}",shortLinkCode);
            return null;
        }
        LinkVo linkVo = new LinkVo();
        BeanUtils.copyProperties(shortLink,linkVo);
        return linkVo;
    }

    @Override
    public JsonData createShortLink(ShortLinkAddRequest request) {
        Long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
        EventMessage eventMessage = EventMessage.builder().accountNo(accountNo)
                .content(JsonUtil.obj2Json(request))
                .messageId(IdUtil.generateSnowFlakeId().toString())
                .eventMessageType(EventMessageType.SHORT_LINK_ADD.name())
                .build();
        rabbitTemplate.convertAndSend(rabbitMQConfig.getShortLinkEventExchange(),rabbitMQConfig.getShortLinkAddRoutingKey(),eventMessage);
        return JsonData.buildSuccess();
    }
}
