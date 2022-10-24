package com.leih.url.link.listener;

import com.leih.url.common.enums.EventMessageTypeEnum;
import com.leih.url.common.model.EventMessage;
import com.leih.url.link.service.ShortLinkService;
import com.leih.url.link.vo.LinkVo;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RabbitListener(queuesToDeclare ={@Queue("short_link.check.queue")})
public class ShortLinkCheckListener {
    @Autowired
    ShortLinkService shortLinkService;
    @RabbitHandler
    public void shortLinkCheckHandler(EventMessage eventMessage, Message message, Channel channel){
        LinkVo linkVo = shortLinkService.parseShortLinkCode(eventMessage.getBizId());
        if(linkVo==null){
            log.info("Link was not successfully shortened, now roll back the group mapping table for the short link code:{}",eventMessage.getBizId());
            eventMessage.setEventMessageType(EventMessageTypeEnum.SHORT_LINK_DELETE_MAPPING.name());
            shortLinkService.deleteShortLinkMappingByCode(eventMessage);
        }
    }
}
