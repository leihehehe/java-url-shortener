package com.leih.url.link.listener;

import com.leih.url.common.enums.BizCodeEnum;
import com.leih.url.common.enums.EventMessageType;
import com.leih.url.common.exception.BizException;
import com.leih.url.common.model.EventMessage;
import com.leih.url.link.config.RabbitMQConfig;
import com.leih.url.link.service.ShortLinkService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
@RabbitListener(queuesToDeclare = {@Queue("short_link.add.link.queue")})
public class ShortLinkAddLinkMQListener {
    @Autowired
    private ShortLinkService shortLinkService;
    @RabbitHandler
    public void shortLinkHandler(EventMessage eventMessage, Message message, Channel channel) throws IOException {
        log.info("Message received by ShortLinkAddLinkMQListener: {}",message);
        try{
            eventMessage.setEventMessageType(EventMessageType.SHORT_LINK_ADD_LINK.name());
            shortLinkService.handleAddShortLink(eventMessage);
        }catch (Exception e){
            log.error("Failed to handle message: {}",eventMessage);
            throw new BizException(BizCodeEnum.MQ_CONSUMER_EXCEPTION);
        }
        log.info("Successfully consumed the message by ShortLinkAddLinkMQListener: {}",eventMessage);
        //confirm that message is successful.
        //channel.basicAck(tag,false);
    }
}
