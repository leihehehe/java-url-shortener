package com.leih.url.account.listener;

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
@RabbitListener(queuesToDeclare ={@Queue("plan.error.queue")})
public class PlanErrorListener {
    @RabbitHandler
    public void orderErrorHandler(EventMessage eventMessage, Message message, Channel channel) throws IOException {
        log.error("Received messages in PlanErrorListener, eventMessage: {}",eventMessage);
        //Exception message
        log.error("Message: {}",message);
        log.warn("Alert!!");
    }
}
