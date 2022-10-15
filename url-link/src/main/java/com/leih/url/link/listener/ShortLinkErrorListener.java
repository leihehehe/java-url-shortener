package com.leih.url.link.listener;

import com.leih.url.common.enums.BizCodeEnum;
import com.leih.url.common.exception.BizException;
import com.leih.url.common.model.EventMessage;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
@RabbitListener(queuesToDeclare ={@Queue("short_link.error.queue")})
public class ShortLinkErrorListener {
    @RabbitHandler
    public void shortLinkHandler(EventMessage eventMessage, Message message, Channel channel) throws IOException {
        log.info("Received messages in ShortLinkErrorMQListener, eventMessage: {}",eventMessage);
        //Exception message
        log.info("Message: {}",message);
        log.info("Alert!!");
    }
}
