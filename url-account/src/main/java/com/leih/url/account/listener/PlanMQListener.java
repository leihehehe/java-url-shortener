package com.leih.url.account.listener;

import com.leih.url.account.service.PlanService;
import com.leih.url.common.enums.BizCodeEnum;
import com.leih.url.common.exception.BizException;
import com.leih.url.common.model.EventMessage;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queuesToDeclare = {@Queue("order.plan.queue"),@Queue("plan.free_init.queue")
,@Queue("plan.restore.queue")})
@Slf4j
public class PlanMQListener {
    @Autowired
    private PlanService planService;
    @RabbitHandler
    public void planHandler(EventMessage eventMessage, Message message, Channel channel){
        log.info("Detected the message in PlanMQListener:{}",eventMessage);
        try{
            planService.handlePlanMessage(eventMessage);
        }catch (Exception e){
            log.error("Failed to consume message:{}",e.getMessage());
            throw new BizException(BizCodeEnum.MQ_CONSUMER_EXCEPTION);
        }
    }
}
